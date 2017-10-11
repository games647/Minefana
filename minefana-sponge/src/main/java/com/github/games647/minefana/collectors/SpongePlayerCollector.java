package com.github.games647.minefana.collectors;

import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.ProtocolVersion;
import com.github.games647.minefana.common.collectors.PlayerCollector;

import java.net.InetAddress;
import java.util.Collection;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public class SpongePlayerCollector extends PlayerCollector<Player> {

    public SpongePlayerCollector(AnalyticsCore core) {
        super(core);
    }

    @Override
    protected Collection<? extends Player> getOnlinePlayers() {
        return Sponge.getServer().getOnlinePlayers();
    }

    @Override
    protected String getLocale(Player player) {
        return player.getLocale().getDisplayName();
    }

    @Override
    protected InetAddress getAddress(Player player) {
        return player.getConnection().getAddress().getAddress();
    }

    @Override
    protected ProtocolVersion getProtocol(Player player) {
        return null;
    }
}
