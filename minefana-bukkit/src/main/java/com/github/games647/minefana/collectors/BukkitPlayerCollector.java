package com.github.games647.minefana.collectors;

import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.AnalyticsPlayer;
import com.github.games647.minefana.common.ProtocolVersion;
import com.github.games647.minefana.common.collectors.PlayerCollector;

import java.net.InetAddress;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import protocolsupport.api.ProtocolSupportAPI;

public class BukkitPlayerCollector extends PlayerCollector<Player, AnalyticsPlayer> {

    private final boolean protocolSupported;

    public BukkitPlayerCollector(AnalyticsCore core) {
        super(core);

        this.protocolSupported = Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport");
    }

    @Override
    protected String getLocale(Player player) {
        return player.getLocale();
    }

    @Override
    protected InetAddress getAddress(Player player) {
        return player.getAddress().getAddress();
    }

    @Override
    protected ProtocolVersion getProtocol(Player player) {
        if (protocolSupported) {
            return ProtocolVersion.getVersion(ProtocolSupportAPI.getProtocolVersion(player).getId());
        }

        return null;
    }

    @Override
    protected boolean isNew(Player player) {
        return !player.hasPlayedBefore();
    }

    @Override
    protected UUID getUUID(Player player) {
        return player.getUniqueId();
    }

    @Override
    protected int getMaxPlayers() {
        return Bukkit.getMaxPlayers();
    }

    @Override
    protected AnalyticsPlayer newAnalyticsPlayer(Player player, String locale, InetAddress address,
                                                 ProtocolVersion protocol) {
        return new AnalyticsPlayer(locale, address, protocol);
    }
}
