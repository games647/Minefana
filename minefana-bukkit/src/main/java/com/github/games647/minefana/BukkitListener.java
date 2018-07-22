package com.github.games647.minefana;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BukkitListener implements Listener {

    private final MinefanaBukkit plugin;

    BukkitListener(MinefanaBukkit plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent joinEvent) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                UUID uuid = joinEvent.getPlayer().getUniqueId();
                Player p = plugin.getServer().getPlayer(uuid);

                if(p != null && p.isOnline()){
                    plugin.getPlayerCollector().onPlayerJoin(p);
                }
            }
        }, 60);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent quitEvent) {
        plugin.getPlayerCollector().onPlayerQuit(quitEvent.getPlayer());
    }
}
