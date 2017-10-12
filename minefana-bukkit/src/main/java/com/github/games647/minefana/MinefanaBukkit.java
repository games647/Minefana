package com.github.games647.minefana;

import com.github.games647.minefana.collectors.BukkitPlayerCollector;
import com.github.games647.minefana.collectors.BukkitWorldCollector;
import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.AnalyticsPlugin;
import com.github.games647.minefana.common.collectors.PingCollector;
import com.github.games647.minefana.common.collectors.TpsCollector;

import java.nio.file.Path;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinefanaBukkit extends JavaPlugin implements AnalyticsPlugin {

    private final Logger logger = LoggerFactory.getLogger(getName());
    private BukkitPlayerCollector playerCollector;

    private AnalyticsCore core;

    @Override
    public void onEnable() {
        core = new AnalyticsCore(this, logger);
        core.saveDefaultConfig();

        if (!core.loadConfig()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        playerCollector = new BukkitPlayerCollector(core);
        registerTasks();
        registerEvents();
    }

    @Override
    public void onDisable() {
        if (core != null) {
            core.close();
        }
    }

    @Override
    public void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BukkitListener(this), this);
    }

    @Override
    public void registerTasks() {
        BukkitScheduler scheduler = getServer().getScheduler();

        TicksPerSecondTask ticksTask = new TicksPerSecondTask();
        scheduler.runTaskTimer(this, ticksTask, 60L, 1L);

        TpsCollector tpsCollector = new TpsCollector(core.getConnector(), ticksTask::getLastTicks);
        scheduler.runTaskTimer(this, tpsCollector, 20L, 20L);

        PingCollector pingTask = new PingCollector(core.getConnector(), () -> Bukkit.getOnlinePlayers()
                .stream()
                .mapToInt(BukkitUtil::getReflectionPing)
                .average()
                .orElse(0));
        scheduler.runTaskTimer(this, pingTask, 40L, 40L);

        scheduler.runTaskTimer(this, new BukkitWorldCollector(core.getConnector()), 5 * 60 * 20L, 5 * 60 * 20L);
        scheduler.runTaskTimer(this, playerCollector, 15 * 60 * 20L, 15 * 60 * 20L);
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

    public BukkitPlayerCollector getPlayerCollector() {
        return playerCollector;
    }
}
