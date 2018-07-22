package com.github.games647.minefana;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BungeeListener implements Listener {

    private final MinefanaBungee plugin;

    BungeeListener(MinefanaBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(ServerConnectedEvent joinEvent) {
        plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
            @Override
            public void run() {
                UUID uuid = joinEvent.getPlayer().getUniqueId();
                ProxiedPlayer p = plugin.getProxy().getPlayer(uuid);

                if(p != null && p.isConnected()){
                    plugin.getPlayerCollector().onPlayerJoin(p);
                }
            }
        },3,TimeUnit.SECONDS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerDisconnectEvent quitEvent) {
        plugin.getPlayerCollector().onPlayerQuit(quitEvent.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerSwitch(ServerSwitchEvent switchEvent) {
        plugin.getPlayerCollector().onServerSwitch(switchEvent);
    }
}
