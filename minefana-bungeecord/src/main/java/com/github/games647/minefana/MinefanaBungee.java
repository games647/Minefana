package com.github.games647.minefana;

import com.github.games647.minefana.collectors.BungeePlayerCollector;
import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.AnalyticsPlugin;
import com.github.games647.minefana.common.collectors.PingCollector;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinefanaBungee extends Plugin implements AnalyticsPlugin {
    private Logger logger;
    private AnalyticsCore core;
    private BungeePlayerCollector playerCollector;

    @Override
    public void onEnable() {
        logger = LoggerFactory.getLogger(getDescription().getName());
        core = new AnalyticsCore(this, logger);
        core.initialize();
    }

    @Override
    public void onDisable() {
        core.close();
    }

    @Override
    public void registerEvents() {
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerListener(this, new BungeeListener(this));
    }

    @Override
    public void registerTasks() {
        TaskScheduler scheduler = getProxy().getScheduler();

        Runnable pingTask = new PingCollector(core.getConnector(), () -> getProxy().getPlayers()
                .stream()
                .mapToInt(ProxiedPlayer::getPing)
                .average().orElse(0));
        scheduler.schedule(this, pingTask, 2, 2, TimeUnit.SECONDS);

        playerCollector = new BungeePlayerCollector(core, this);

        scheduler.schedule(this, playerCollector, 15, 15, TimeUnit.MINUTES);
    }

    @Override
    public Path getPluginFolder() {
        return getDataFolder().toPath();
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
    public BungeePlayerCollector getPlayerCollector() {
        return playerCollector;
    }
}
