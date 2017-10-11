package com.github.games647.minefana.collectors;

import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.ProtocolVersion;
import com.github.games647.minefana.common.collectors.PlayerCollector;

import java.net.InetAddress;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;

public class BukkitPlayerCollector extends PlayerCollector<Player> {

    private final boolean protocolSupported;

    public BukkitPlayerCollector(AnalyticsCore core) {
        super(core);

        this.protocolSupported = Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport");
    }

    @Override
    protected Collection<? extends Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
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
}
