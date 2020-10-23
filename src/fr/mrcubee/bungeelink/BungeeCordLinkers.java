package fr.mrcubee.bungeelink;

import fr.mrcubee.bungeelink.command.LinkerTabExecutor;
import fr.mrcubee.bungeelink.config.ConfigurationManager;
import fr.mrcubee.bungeelink.listeners.RegisterListeners;
import fr.mrcubee.bungeelink.player.ConnectionManager;
import fr.mrcubee.bungeelink.security.KeyManager;
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

    private void loadKeys(Configuration configuration) {
        Configuration keySection;
        Collection<String> keys;

        if (configuration == null)
            return;
        this.keyManager.registerPrivate(configuration.getString("private_key"));
        keySection = configuration.getSection("keys");
        if (keySection == null)
            return;
        keys = keySection.getKeys();
        if (keys.isEmpty())
            return;
        for (String name : keys)
            this.keyManager.register(name, keySection.getString(name));
    }

    private void loadDisableKeys(Configuration configuration) {
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
        loadKeys(this.config);
        loadDisableKeys(this.config);
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