package com.github.games647.minefana;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {

    private final MinefanaBukkit plugin;

    BukkitListener(MinefanaBukkit plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        plugin.getPlayerCollector().onPlayerJoin(joinEvent.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent quitEvent) {
        plugin.getPlayerCollector().onPlayerQuit(quitEvent.getPlayer());
    }

    private void sendPlayerUpdate() {

    }
}
