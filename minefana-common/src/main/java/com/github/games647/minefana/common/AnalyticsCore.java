package com.github.games647.minefana.common;

import com.github.games647.minefana.common.model.Country;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;
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
import java.util.Optional;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.slf4j.Logger;

public class AnalyticsCore implements AutoCloseable {

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
        try (OutputStream out = Files.newOutputStream(outputPath)) {
            Resources.copy(new URL(GEO_DATABASE_URL), out);
        } catch (IOException ioEx) {
            logger.error("Failed to download GEO IP database", ioEx);
            return;
        }

        Path databaseFile = plugin.getPluginFolder().resolve(DATABASE_FILE);
        try {
            decompress(outputPath, databaseFile);
        } catch (IOException | ArchiveException | CompressorException ex) {
            logger.error("Failed to extract GEO IP database", ex);
        }

        try {
            geoReader = new Reader(databaseFile.toFile(), FileMode.MEMORY, new CHMCache());
        } catch (IOException ioEx) {
            logger.error("Failed to read GEO IP database", ioEx);
        }
    }

    private void decompress(Path outputPath, Path outputFile)
            throws IOException, ArchiveException, CompressorException {
        try (
                CompressorInputStream in = new CompressorStreamFactory()
                        .createCompressorInputStream(CompressorStreamFactory.GZIP, Files.newInputStream(outputPath));

                TarArchiveInputStream tarIn = (TarArchiveInputStream) new ArchiveStreamFactory()
                        .createArchiveInputStream("tar", in)
        ) {

            while (tarIn.getNextEntry() != null) {
                TarArchiveEntry current = tarIn.getCurrentEntry();
                if (!current.isFile()) {
                    continue;
                }

                if (current.getName().endsWith(outputFile.getFileName().toString())) {
                    if (Files.notExists(outputFile)) {
                        Files.createFile(outputFile);
                    }

                    try (OutputStream out = Files.newOutputStream(outputFile)) {
                        int count;
                        byte data[] = new byte[1024];
                        while ((count = tarIn.read(data, 0, 1024)) != -1) {
                            out.write(data, 0, count);
                        }
                    }

                    break;
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

    @Override
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
