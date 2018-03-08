package com.github.games647.minefana.common;

import com.github.games647.minefana.common.model.Country;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;
import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;
import com.maxmind.db.CHMCache;
import com.maxmind.db.Reader;
import com.maxmind.db.Reader.FileMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import org.slf4j.Logger;

public class AnalyticsCore {

    private static final String CONFIG_FILE_NAME = "config.yml";

    private static final String DATABASE_NAME = "GeoLite2-Country";
    private static final String DATABASE_FILE = DATABASE_NAME + ".mmdb";

    private static final String ARCHIVE_FILE = DATABASE_NAME + ".tar.gz";
    private static final String GEO_DATABASE_URL = "https://geolite.maxmind.com/download/geoip/database/"
            + ARCHIVE_FILE;

    private final AnalyticsPlugin plugin;
    private final Logger logger;

    private InfluxConnector connector;
    private Reader geoReader;

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
            Configuration config = provider.load(Files.newBufferedReader(configFile));
            String dbUrl = config.getString("db_url");
            String dbName = config.getString("db_name");
            String dbUser = config.getString("db_user");
            String dbPass = config.getString("db_pass");

            if (config.getBoolean("geo-ip")) {
                loadGeo();
            }

            connector = new InfluxConnector(dbUrl, dbUser, dbPass, dbName);
            connector.init();

            return true;
        } catch (IOException ioEx) {
            logger.error("Failed to load config", ioEx);
        }

        return false;
    }

    private void saveDefaultConfig() {
        Path dataFolder = plugin.getPluginFolder();

        try {
            if (Files.notExists(dataFolder)) {
                Files.createDirectories(dataFolder);
            }

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
        Path outputPath = plugin.getPluginFolder().resolve(ARCHIVE_FILE);
        if (Files.exists(outputPath)) {
            try {
                Instant lastModified = Files.getLastModifiedTime(outputPath).toInstant();

                long days = Duration.between(lastModified, Instant.now()).toDays();
                if (days > 1) {
                    downloadDatabase(outputPath);
                }
            } catch (IOException e) {
                logger.error("Failed to read last modification time of {}. Skipping geo IP", outputPath);
                return;
            }
        }

        Path databaseFile = plugin.getPluginFolder().resolve(DATABASE_FILE);
        try {
            decompress(outputPath, databaseFile);
        } catch (IOException ex) {
            logger.error("Failed to extract GEO IP database", ex);
            return;
        }

        try {
            geoReader = new Reader(databaseFile.toFile(), FileMode.MEMORY, new CHMCache());
        } catch (IOException ioEx) {
            logger.error("Failed to read GEO IP database", ioEx);
        }
    }

    private void downloadDatabase(Path outputPath) {
        try (OutputStream out = Files.newOutputStream(outputPath)) {
            Resources.copy(new URL(GEO_DATABASE_URL), out);
        } catch (IOException ioEx) {
            logger.error("Failed to download GEO IP database", ioEx);
        }
    }

    private void decompress(Path input, Path outputFile) throws IOException {
        try (TarInputStream in = new TarInputStream(new GZIPInputStream(Files.newInputStream(input)))) {
            TarEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String filename = entry.getName();
                    if (DATABASE_FILE.equals(filename)) {
                        Files.copy(in, outputFile);
                        break;
                    }
                }
            }
        }
    }

    public Optional<Country> getCountry(InetAddress address) {
        if (geoReader == null) {
            return Optional.empty();
        }

        try {
            JsonElement jsonElement = geoReader.get(address);
            return Optional.of(Country.of(jsonElement));
        } catch (Exception ex) {
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
