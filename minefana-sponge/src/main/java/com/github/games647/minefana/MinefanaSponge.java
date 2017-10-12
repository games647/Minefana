package com.github.games647.minefana;

import com.github.games647.minefana.collectors.SpongePlayerCollector;
import com.github.games647.minefana.collectors.SpongeWorldCollector;
import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.AnalyticsPlugin;
import com.github.games647.minefana.common.collectors.PingCollector;
import com.github.games647.minefana.common.collectors.TpsCollector;
import com.google.inject.Inject;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

@Plugin(
        id = "minefana-sponge",
        name = "MineFana",
        version = "1.0-SNAPSHOT",
        description = "Sends minecraft statistics to a InfluxDB to be displayed by grafana"
)
public class MinefanaSponge implements AnalyticsPlugin {

    private final Logger logger;
    private final AnalyticsCore core;
    private final SpongePlayerCollector playerCollector;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path pluginFolder;

    @Inject
    public MinefanaSponge(Logger logger) {
        this.logger = logger;
        this.core = new AnalyticsCore(this, logger);
        this.playerCollector = new SpongePlayerCollector(core);
    }

    @Listener
    public void onServerInit(GameInitializationEvent initEvent) {
        core.initialize();
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent stoppingEvent) {
        core.close();
    }

    @Override
    public void registerEvents() {
        Sponge.getEventManager().registerListeners(this, new SpongeListener(this));
    }

    @Override
    public void registerTasks() {
        Task.builder()
                .interval(1, TimeUnit.SECONDS)
                .execute(new TpsCollector(core.getConnector(), Sponge.getServer()::getTicksPerSecond))
                .submit(this);

        Task.builder()
                .interval(2, TimeUnit.SECONDS)
                .execute(new PingCollector(core.getConnector(), () -> Sponge.getServer().getOnlinePlayers().stream()
                        .mapToInt(player -> player.getConnection().getLatency())
                        .average().orElse(0)))
                .submit(this);

        Task.builder().interval(5, TimeUnit.MINUTES)
                .execute(new SpongeWorldCollector(core.getConnector()))
                .submit(this);

        Task.builder().interval(15, TimeUnit.MINUTES)
                .execute(playerCollector).submit(this);
    }

    @Override
    public Path getPluginFolder() {
        return pluginFolder;
    }

    @Override
    public Logger getLog() {
        return logger;
    }

    @Override
    public AnalyticsCore getCore() {
        return core;
    }

    @Override
    public SpongePlayerCollector getPlayerCollector() {
        return playerCollector;
    }
}
