package fr.mrcubee.bungeelink.security;

import com.mysql.jdbc.StringUtils;
import io.netty.util.internal.StringUtil;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.logging.Logger;

public class KeyManager {

    private File keyFolder;
    private Logger logger;

    private PrivateKey privateKey;
    private HashMap<String, PublicKey> keys;
    private HashMap<String, String> keysFileName;
    private Set<String> disabledKeys;

    public KeyManager(File keyFolder, Logger logger) {
        this.keyFolder = (keyFolder != null) ? keyFolder : new File(".");
        this.logger = logger;
        this.keys = new HashMap<String, PublicKey>();
        this.keysFileName = new HashMap<String, String>();
        this.disabledKeys = new HashSet<String>();
    }

    public boolean register(String name, PublicKey publicKey) {
        if (publicKey == null || StringUtils.isEmptyOrWhitespaceOnly(name) || this.keys.containsKey(name))
            return false;
        this.keys.put(name, publicKey);
        this.logger.info("Register " + name + " key.");
        return true;
    }

    public boolean register(String name, String fileName) {
        File keyFile;
        String keyPath;
        String keyFolderPath;

        if (StringUtils.isEmptyOrWhitespaceOnly(fileName))
            return false;
        keyFile = new File(this.keyFolder, fileName);
        try {
            keyPath = keyFile.getCanonicalPath();
            keyFolderPath = this.keyFolder.getCanonicalPath();
        } catch (IOException ignored) {
            return false;
        }
        if (!keyFile.exists() || !keyFile.isFile() || !keyPath.startsWith(keyFolderPath) || isLoaded(fileName))
            return false;
        if (register(name, KeyUtils.loadPublicKey(keyFile.toURI()))) {
            this.keysFileName.put(name, keyPath.substring(keyFolderPath.length() + 1));
            return true;
        }
        return false;
    }

    public boolean unRegister(String name) {
        if (name == null)
            return false;
        this.disabledKeys.remove(name);
        this.keysFileName.remove(name);
        if (this.keys.remove(name) != null) {
            this.logger.info("Unregister " +  name + " key.");
            return true;
        }
        return false;
    }

    public boolean setDisable(String name, boolean disable) {
        if (name == null || (disable && StringUtils.isEmptyOrWhitespaceOnly(name)))
            return false;
        else if (disable) {
            this.logger.info(name + " key disabled.");
            return this.disabledKeys.add(name);
        }
        this.logger.info(name + " key enabled.");
        return this.disabledKeys.remove(name);
    }

    public boolean isDisabled(String name) {
        return (name != null && this.disabledKeys.contains(name));
    }

    public boolean isLoaded(String fileName) {
        File keyFile;
        String keyPath;
        String keyFolderPath;

        if (StringUtils.isEmptyOrWhitespaceOnly(fileName))
            return false;
        keyFile = new File(this.keyFolder, fileName);
        try {
            keyPath = keyFile.getCanonicalPath();
            keyFolderPath = this.keyFolder.getCanonicalPath();
        } catch (IOException ignored) {
            return false;
        }
        if (!keyFile.exists() || !keyFile.isFile() || !keyPath.startsWith(keyFolderPath))
            return false;
        return this.keysFileName.containsValue(keyPath.substring(keyFolderPath.length() + 1));
    }

    public String getSignatureOwner(byte[] signatureBytes, byte[] bytes) {
        if (signatureBytes == null || signatureBytes.length < 1 || bytes == null || bytes.length < 1)
            return null;
        for (Map.Entry<String, PublicKey> entry : this.keys.entrySet())
            if (KeyUtils.checkSignature(entry.getValue(), signatureBytes, bytes))
                return entry.getKey();
        return null;
    }

    public String getSignatureEnabledOwner(byte[] signatureBytes, byte[] bytes) {
        String owner = getSignatureOwner(signatureBytes, bytes);

        return ((owner != null && !this.disabledKeys.contains(owner)) ? owner : null);
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getKey(String name) {
        if (StringUtils.isEmptyOrWhitespaceOnly(name))
            return null;
        return this.keys.get(name);
    }

    public Set<String> getKeyNames() {
        return new HashSet<String>(this.keys.keySet());
    }

    public Configuration toConfiguration() {
        Configuration configuration = new Configuration();
        List<String> disableKeys = new ArrayList<String>(this.disabledKeys);

        for (Map.Entry<String, String> entry : this.keysFileName.entrySet())
            configuration.set("keys." + entry.getKey(), entry.getValue());
        configuration.set("disable", disableKeys);
        return configuration;
    }
}
