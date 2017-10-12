package com.github.games647.minefana;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeListener implements Listener {

    private final MinefanaBungee plugin;

    BungeeListener(MinefanaBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(ServerConnectedEvent joinEvent) {
        plugin.getPlayerCollector().onPlayerJoin(joinEvent.getPlayer());
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
