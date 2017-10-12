package com.github.games647.minefana;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class SpongeListener {

    private final MinefanaSponge plugin;

    SpongeListener(MinefanaSponge plugin) {
        this.plugin = plugin;
    }

    @Listener(order = Order.POST)
    public void onPlayerJoin(ClientConnectionEvent.Join joinEvent) {
        plugin.getPlayerCollector().onPlayerJoin(joinEvent.getTargetEntity());
    }

    @Listener(order = Order.POST)
    public void onPlayerQuit(ClientConnectionEvent.Disconnect quitEvent) {
        plugin.getPlayerCollector().onPlayerQuit(quitEvent.getTargetEntity());
    }
}
