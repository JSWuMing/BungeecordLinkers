package fr.mrcubee.bungeelink;

import fr.mrcubee.bungeelink.command.LinkerTabExecutor;
import fr.mrcubee.bungeelink.config.ConfigurationManager;
import fr.mrcubee.bungeelink.listeners.RegisterListeners;
import fr.mrcubee.bungeelink.player.ConnectionManager;
import fr.mrcubee.bungeelink.security.KeyManager;
import fr.mrcubee.bungeelink.security.KeyUtils;
import fr.mrcubee.bungeelink.stats.PluginStats;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BungeeCordLinkers extends Plugin {

    private File keyFolder;
    private KeyManager keyManager;
    private ConnectionManager connectionManager;
    private Configuration config;

    @Override
    public void onLoad() {
        this.keyFolder = new File(this.getDataFolder(), "keys/");
        if (!this.keyFolder.exists())
            this.keyFolder.mkdirs();
        this.keyManager = new KeyManager(this.keyFolder, this.getLogger());
        this.connectionManager = new ConnectionManager();
    }

    private void loadPrivateKeyFromConfig(Configuration configuration) {
        File keyFile;

        if (configuration == null)
            return;
        keyFile = new File(this.keyFolder, configuration.getString("private_key"));
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
        for (String name : configuration.getKeys())
            this.keyManager.register(name, configuration.getString(name));
    }

    private void loadDisableKey(Configuration configuration) {
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
        this.getProxy().getScheduler().schedule(this, PluginStats.createNew(this), 0L, 2L, TimeUnit.MINUTES);
        this.getProxy().getPluginManager().registerCommand(this, new LinkerTabExecutor(this));
        if (getConfig() == null) {
            this.getLogger().severe("Config Error !");
            return;
        }
        loadPrivateKeyFromConfig(this.config);
        loadPublicKeysFromConfig(this.config.getSection("keys"));
        loadDisableKey(this.config);
        RegisterListeners.register(this);
    }

    public KeyManager getKeyManager() {
        return this.keyManager;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public File getKeyFolder() {
        return keyFolder;
    }

    public Configuration getConfig() {
        if (this.config == null)
            this.config = ConfigurationManager.getConfig(this, "config");
        return this.config;
    }

    public void saveConfig() {
        ConfigurationManager.saveConfig(this, this.keyManager.toConfiguration(), "config");
    }
}