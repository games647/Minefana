package com.github.games647.minefana.common;

import com.google.common.io.Resources;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.record.Country;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import org.slf4j.Logger;

public class AnalyticsCore {

    private static final String CONFIG_FILE_NAME = "config.yml";
    private static final String GEO_DATABASE_URL = "https://geolite.maxmind.com/" +
            "download/geoip/database/GeoLite2-Country.tar.gz";

    private final AnalyticsPlugin plugin;
    private final Logger logger;

    private InfluxConnector connector;
    private DatabaseReader geoReader;

    public AnalyticsCore(AnalyticsPlugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    public boolean initialize() {
        saveDefaultConfig();

        if (!loadConfig()) {
            return false;
        }

        plugin.registerTasks();
        plugin.registerEvents();
        return true;
    }

    private boolean loadConfig() {
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

            if (config.getBoolean("geo-ip")) {
                loadGeo();
            }

            return true;
        } catch (IOException ioEx) {
            logger.error("Failed to load config", ioEx);
        }

        return false;
    }

    private void saveDefaultConfig() {
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

    private void loadGeo() {
        Path outputPath = plugin.getPluginFolder().resolve("GeoLite2-Country.tar.gz");
        try (OutputStream out = Files.newOutputStream(outputPath)) {
            Resources.copy(new URL(GEO_DATABASE_URL), out);
        } catch (IOException ioEx) {
            logger.error("Failed to download GEO IP database", ioEx);
            return;
        }

        try {
            geoReader = new DatabaseReader.Builder(outputPath.toFile()).withCache(new CHMCache()).build();
        } catch (IOException ioEx) {
            logger.error("Failed to read GEO IP database", ioEx);
        }
    }

    public Optional<Country> getCountry(InetAddress address) {
        if (geoReader == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(geoReader.country(address).getCountry());
        } catch (AddressNotFoundException notFoundEx) {
            //ignore
        } catch (IOException | GeoIp2Exception ex) {
            logger.error("Failed to lookup country of {}", address, ex);
        }

        return Optional.empty();
    }

    public InfluxConnector getConnector() {
        return connector;
    }

    public void close() {
        if (connector != null) {
            connector.close();
        }

        if (geoReader != null) {
            try {
                geoReader.close();
            } catch (IOException ioEx) {
                logger.error("Failed to close geo reader", ioEx);
            }
        }
    }
}
