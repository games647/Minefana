package com.github.games647.minefana;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import org.influxdb.dto.Point;

public class PlayerListener implements Listener {

    private final MinefanaBungeeCord plugin;

    public PlayerListener(MinefanaBungeeCord plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(ServerConnectedEvent joinEvent) {
        sendPlayerUpdate();
    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent quitEvent) {
        sendPlayerUpdate();
    }

    private void sendPlayerUpdate() {
        Point playerPoint = Point.measurement("players")
                .addField("online", ProxyServer.getInstance().getPlayers().size())
                .addField("max", ProxyServer.getInstance().getConfig().getPlayerLimit())
                .build();

        plugin.getInfluxConnector().send(playerPoint);
    }
}
