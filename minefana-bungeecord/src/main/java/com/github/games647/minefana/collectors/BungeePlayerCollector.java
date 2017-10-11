package com.github.games647.minefana.collectors;

import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.ProtocolVersion;
import com.github.games647.minefana.common.collectors.PlayerCollector;

import java.net.InetAddress;
import java.util.Collection;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePlayerCollector extends PlayerCollector<ProxiedPlayer> {

    public BungeePlayerCollector(AnalyticsCore core) {
        super(core);
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    protected Collection<? extends ProxiedPlayer> getOnlinePlayers() {
        return ProxyServer.getInstance().getPlayers();
    }

    @Override
    protected String getLocale(ProxiedPlayer player) {
        return player.getLocale().getDisplayName();
    }

    @Override
    protected InetAddress getAddress(ProxiedPlayer player) {
        return player.getAddress().getAddress();
    }

    @Override
    protected ProtocolVersion getProtocol(ProxiedPlayer player) {
        return ProtocolVersion.getVersion(player.getPendingConnection().getVersion());
    }
}
