package fr.mrcubee.bungeelink.security;

import com.mysql.jdbc.StringUtils;
import net.md_5.bungee.api.plugin.Plugin;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class KeyManager {

    private Logger logger;

    private PrivateKey privateKey;
    private HashMap<String, PublicKey> keys;
    private Set<String> disabledKeys;

    public KeyManager(Logger logger) {
        this.logger = logger;
        this.keys = new HashMap<String, PublicKey>();
        this.disabledKeys = new HashSet<String>();
    }

    public boolean register(String name, PublicKey publicKey) {
        if (publicKey == null || StringUtils.isEmptyOrWhitespaceOnly(name) || this.keys.containsKey(name))
            return false;
        this.keys.put(name, publicKey);
        this.logger.info("Register " + name + " key.");
        return true;
    }

    public boolean unRegister(String name) {
        if (name != null && this.keys.remove(name) != null) {
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
}
