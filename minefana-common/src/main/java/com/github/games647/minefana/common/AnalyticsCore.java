package com.github.games647.minefana.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import org.slf4j.Logger;

public class AnalyticsCore {

    private static final String CONFIG_FILE_NAME = "config.yml";

    private final AnalyticsPlugin plugin;
    private final Logger logger;

    private InfluxConnector connector;

    public AnalyticsCore(AnalyticsPlugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    public boolean loadConfig() {
        ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

        Path configFile = plugin.getPluginFolder().resolve(CONFIG_FILE_NAME);
        try {
            Configuration config = provider.load(configFile.toFile());
            String dbUrl = config.getString("db_url");
            String dbName = config.getString("db_name");
            String dbUser = config.getString("db_user");
            String dbPass = config.getString("db_pass");

            connector = new InfluxConnector(dbUrl, dbName, dbUser, dbPass);
            connector.init();

            return true;
        } catch (IOException ioEx) {
            logger.error("Failed to load config", ioEx);
        }

        return false;
    }

    public void saveDefaultConfig() {
        Path dataFolder = plugin.getPluginFolder();

        try {
            Files.createDirectories(dataFolder);

            Path configFile = dataFolder.resolve(CONFIG_FILE_NAME);
            if (Files.notExists(configFile)) {
                try (InputStream defaultStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
                    Files.copy(defaultStream, configFile);
                }
            }
        } catch (IOException ioEx) {
            logger.error("Failed to save default config", ioEx);
        }
    }

    public InfluxConnector getConnector() {
        return connector;
    }

    public void close() {
        if (connector != null) {
            connector.close();
        }
    }
}
