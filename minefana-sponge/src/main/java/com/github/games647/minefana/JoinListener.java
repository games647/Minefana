package com.github.games647.minefana;

import com.github.games647.minefana.common.AnalyticsType;

import org.influxdb.dto.Point;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;

public class JoinListener {

    private final MinefanaSponge plugin;

    public JoinListener(MinefanaSponge plugin) {
        this.plugin = plugin;
    }

    @Listener(order = Order.POST)
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Task.builder().delayTicks(1).execute(this::updatePlayersStat).submit(this);
    }

    @Listener(order = Order.POST)
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Task.builder().delayTicks(1).execute(this::updatePlayersStat).submit(this);
    }

    private void updatePlayersStat() {
        Point playerPoint = AnalyticsType.PLAYERS.newPoint()
                .addField("online", Sponge.getServer().getOnlinePlayers().size())
                .addField("max", Sponge.getServer().getMaxPlayers())
                .build();

        plugin.getCore().getConnector().send(playerPoint);
    }
}
