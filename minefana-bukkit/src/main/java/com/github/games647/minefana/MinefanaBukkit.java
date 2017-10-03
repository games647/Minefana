package com.github.games647.minefana;

import com.github.games647.minefana.common.InfluxConnector;
import com.github.games647.minefana.common.MeasurementType;

import org.bukkit.plugin.java.JavaPlugin;
import org.influxdb.dto.Point;

public class MinefanaBukkit extends JavaPlugin {

    private InfluxConnector influxConnector;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        String dbUrl = getConfig().getString("db_url");
        String dbName = getConfig().getString("db_name");
        String dbUser = getConfig().getString("db_user");
        String dbPass = getConfig().getString("db_pass");

        influxConnector = new InfluxConnector(dbUrl, dbName, dbUser, dbPass);
        influxConnector.init();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        TicksPerSecondTask task = new TicksPerSecondTask();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, task, 60L, 1L);

        getServer().getScheduler()
                .scheduleSyncRepeatingTask(this, () -> influxConnector
                        .send(Point.measurement(MeasurementType.TPS.getId())
                                .addField("ticks per second", task.getLastTicks())
                                .build()), 60L, 60L);
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
