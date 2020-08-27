package fr.mrcubee.bungeelink.config;

import com.mysql.jdbc.StringUtils;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigurationManager {


    private static boolean extractResource(Plugin plugin, String resourceName, File target) {
        InputStream inputStream;
        boolean result = true;

        if (StringUtils.isEmptyOrWhitespaceOnly(resourceName) || target == null)
            return false;
        inputStream = plugin.getClass().getClassLoader().getResourceAsStream(resourceName + ".yml");
        if (inputStream == null)
            return false;
        try {
            Files.copy(inputStream, Paths.get(target.toURI()));
        } catch (IOException ignored) {
            result = false;
        }
        try {
            inputStream.close();
        } catch (IOException ignored) {}
        return result;
    }

    public static Configuration getConfig(Plugin plugin, String fileName) {
        File configFile;

        if (plugin == null || StringUtils.isEmptyOrWhitespaceOnly(fileName))
            return null;
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdirs();
        configFile = new File(plugin.getDataFolder(), fileName + ".yml");
        if (!configFile.exists() && !extractResource(plugin, fileName, configFile))
            return null;
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException ignored) {}
        return null;
    }

    public static boolean saveConfig(Plugin plugin, Configuration configuration, String fileName) {
        File configFile;

        if (plugin == null || configuration == null || StringUtils.isEmptyOrWhitespaceOnly(fileName))
            return false;
        configFile = new File(plugin.getDataFolder(), fileName + ".yml");

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
            return true;
        } catch (IOException ignored) {}
        return false;
    }

}
