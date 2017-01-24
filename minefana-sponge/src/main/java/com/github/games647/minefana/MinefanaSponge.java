package com.github.games647.minefana;

import com.github.games647.minefana.common.InfluxConnector;
import com.google.inject.Inject;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "minefana-sponge",
        name = "MineFana",
        version = "1.0-SNAPSHOT",
        description = "Sends minecraft statistics to a InfluxDB to be displayed by grafana"
)
public class MinefanaSponge {

    @Inject
    private Logger logger;

    @Inject
    private Game game;

    private InfluxConnector influxConnector;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path config;

    @Listener
    public void onServerInit(GameInitializationEvent initEvent) {
        ConfigurationNode rootNode = loadConfig();
        if (rootNode != null) {
            String dbUrl = rootNode.getNode("db_url").getString();
            String dbName = rootNode.getNode("db_name").getString();
            String dbUser = rootNode.getNode("db_user").getString();
            String dbPass = rootNode.getNode("db_pass").getString();

            influxConnector = new InfluxConnector(dbUrl, dbName, dbUser, dbPass);
            influxConnector.init();
        }

        game.getScheduler().createTaskBuilder()
                .interval(1, TimeUnit.SECONDS)
                .execute(() -> influxConnector.send(Point.measurement("tps")
                        .addField("ticks per second", game.getServer().getTicksPerSecond())
                        .build()))
                .submit(this);
    }

    private ConfigurationNode loadConfig() {
        HoconConfigurationLoader hoconLoader = HoconConfigurationLoader.builder().setPath(config).build();
        ConfigurationNode rootNode = null;
        if (Files.notExists(config)) {
            URL defaultConfig = getClass().getResource("config.yml");

            YAMLConfigurationLoader yamlLoader = YAMLConfigurationLoader.builder().setURL(defaultConfig).build();
            try {
                rootNode = yamlLoader.load();
                //save the default file
                hoconLoader.save(rootNode);
            } catch (IOException ioEx) {
                logger.warn("Error saving default config {}", ioEx);
            }
        } else {
            try {
                rootNode = hoconLoader.load();
            } catch (IOException ioEx) {
                logger.warn("Error loading config {}", ioEx);
            }
        }

        return rootNode;
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent stoppingEvent) {
        if (influxConnector != null) {
            influxConnector.close();
        }
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
        game.getScheduler().createTaskBuilder().delayTicks(1).execute(this::updatePlayersStat).submit(this);
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
        game.getScheduler().createTaskBuilder().delayTicks(1).execute(this::updatePlayersStat).submit(this);
    }

    private void updatePlayersStat() {
        Point playerPoint = Point.measurement("players")
                .addField("online", game.getServer().getOnlinePlayers().size())
                .addField("max", game.getServer().getMaxPlayers())
                .build();

        influxConnector.send(playerPoint);
    }
}
