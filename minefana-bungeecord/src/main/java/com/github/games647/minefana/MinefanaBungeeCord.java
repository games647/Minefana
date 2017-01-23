package com.github.games647.minefana;

import com.github.games647.minefana.common.InfluxConnector;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public final class MinefanaBungeeCord extends Plugin {

    private InfluxConnector influxConnector;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        try {
            File configFile = new File(getDataFolder(), "config.yml");
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            String dbUrl = config.getString("db_url");
            String dbName = config.getString("db_name");
            String dbUser = config.getString("db_user");
            String dbPass = config.getString("db_pass");

            influxConnector = new InfluxConnector(dbUrl, dbName, dbUser, dbPass);
            influxConnector.init();

            getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
        } catch (IOException ioEx) {
            getLogger().log(Level.WARNING, "Couldn't load config", ioEx);
        }
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public InfluxConnector getInfluxConnector() {
        return influxConnector;
    }

    @Override
    public void onDisable() {
        if (influxConnector != null) {
            influxConnector.close();
        }
    }
}
