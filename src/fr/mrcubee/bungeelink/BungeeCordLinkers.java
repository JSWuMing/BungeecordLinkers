package fr.mrcubee.bungeelink;

import fr.mrcubee.bungeelink.config.ConfigurationManager;
import fr.mrcubee.bungeelink.listeners.RegisterListeners;
import fr.mrcubee.bungeelink.player.ConnectionManager;
import fr.mrcubee.bungeelink.security.KeyManager;
import fr.mrcubee.bungeelink.security.KeyUtils;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.security.PublicKey;
import java.util.Collection;
import java.util.List;

public class BungeeCordLinkers extends Plugin {

    private KeyManager keyManager;
    private ConnectionManager connectionManager;
    private Configuration config;

    @Override
    public void onLoad() {
        this.keyManager = new KeyManager(this.getLogger());
        this.connectionManager = new ConnectionManager();
    }

    private void loadPrivateKeyFromConfig(Configuration configuration) {
        File keyFile;

        if (configuration == null)
            return;
        keyFile = new File(this.getDataFolder(), "keys/" + configuration.getString("private_key"));
        if (!keyFile.exists() || !keyFile.isFile())
            return;
        this.getKeyManager().setPrivateKey(KeyUtils.loadPrivateKey(keyFile.toURI()));
    }

    private void loadPublicKeysFromConfig(Configuration configuration) {
        Collection<String> keys;
        File keyFile;

        if (configuration == null)
            return;
        keys = configuration.getKeys();
        if (keys == null || keys.isEmpty())
            return;
        for (String name : configuration.getKeys()) {
            keyFile = new File(this.getDataFolder(), "keys/" + configuration.get(name));
            this.keyManager.register(name, KeyUtils.loadPublicKey(keyFile.toURI()));
        }
    }

    private void disableKey(Configuration configuration) {
        List<String> keyDisabled;

        if (configuration == null)
            return;
        keyDisabled = configuration.getStringList("disable");
        if (keyDisabled == null || keyDisabled.isEmpty())
            return;
        for (String keyName : keyDisabled)
            this.keyManager.setDisable(keyName, true);
    }

    @Override
    public void onEnable() {
        File keyFolder;

        this.config = ConfigurationManager.getConfig(this, "config");
        if (this.config == null) {
            this.getLogger().severe("Config Error !");
            return;
        }
        keyFolder = new File(this.getDataFolder(), "keys/");
        if (!keyFolder.exists())
            keyFolder.mkdirs();
        loadPrivateKeyFromConfig(this.config);
        loadPublicKeysFromConfig(this.config.getSection("keys"));
        RegisterListeners.register(this);
    }

    @Override
    public void onDisable() {
        ConfigurationManager.saveConfig(this, config, "config");
    }

    public KeyManager getKeyManager() {
        return this.keyManager;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }
}