package com.github.games647.minefana.collectors;

import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.AnalyticsPlayer;
import com.github.games647.minefana.common.ProtocolVersion;
import com.github.games647.minefana.common.collectors.PlayerCollector;

import java.net.InetAddress;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public class SpongePlayerCollector extends PlayerCollector<Player, AnalyticsPlayer> {

    public SpongePlayerCollector(AnalyticsCore core) {
        super(core);
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

    @Override
    protected UUID getUUID(Player player) {
        return player.getUniqueId();
    }

    @Override
    protected int getMaxPlayers() {
        return Sponge.getServer().getMaxPlayers();
    }

    @Override
    protected AnalyticsPlayer newAnalyticsPlayer(Player player, String locale, InetAddress address,
                                                 ProtocolVersion protocol) {
        return new AnalyticsPlayer(locale, address, protocol);
    }
}
