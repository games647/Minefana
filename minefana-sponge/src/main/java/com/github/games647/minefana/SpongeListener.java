package com.github.games647.minefana;

import com.google.inject.Inject;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class SpongeListener {

    private final MinefanaSponge plugin;

    @Inject
    SpongeListener(MinefanaSponge plugin) {
        this.plugin = plugin;
    }

    @Listener(order = Order.POST)
    public void onPlayerJoin(ClientConnectionEvent.Join joinEvent) {
         Task.Builder taskBuild = Task.builder();
         taskBuild.execute(new Runnable() {
            @Override
            public void run() {
                UUID uuid = joinEvent.getTargetEntity().getUniqueId();
                Optional<Player> playerOptional = Sponge.getServer().getPlayer(uuid);

                if(playerOptional.isPresent()){
                    Player p = playerOptional.get();
                    if(p != null && p.isOnline()){
                        plugin.getPlayerCollector().onPlayerJoin(p);
                    }
                }
            }
        }).delayTicks(60).submit(plugin);
    }

    @Listener(order = Order.POST)
    public void onPlayerQuit(ClientConnectionEvent.Disconnect quitEvent) {
        plugin.getPlayerCollector().onPlayerQuit(quitEvent.getTargetEntity());
    }
}
