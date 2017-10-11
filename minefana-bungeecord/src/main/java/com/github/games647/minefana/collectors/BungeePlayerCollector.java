package com.github.games647.minefana.collectors;

import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.AnalyticsType;
import com.github.games647.minefana.common.ProtocolVersion;
import com.github.games647.minefana.common.collectors.PlayerCollector;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public class BungeePlayerCollector extends PlayerCollector<ProxiedPlayer> {

    public BungeePlayerCollector(AnalyticsCore core) {
        super(core);
    }

    @Override
    public void run() {
        super.run();

        int forgeUsers = (int) getOnlinePlayers().stream().filter(ProxiedPlayer::isForgeUser).count();
        send(AnalyticsType.FORGE_USER.newPoint().addField("users", forgeUsers));

        addFields(AnalyticsType.FORGE_MODS, getOnlinePlayers().stream()
                .map(ProxiedPlayer::getModList)
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )));

        addFields(AnalyticsType.BUNGEE_PLAYER_PER_SERVER, getOnlinePlayers().stream()
                .map(ProxiedPlayer::getServer)
                .map(Server::getInfo)
                .map(ServerInfo::getName)
                .collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )));
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
