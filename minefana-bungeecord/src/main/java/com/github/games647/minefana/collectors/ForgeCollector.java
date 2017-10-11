package com.github.games647.minefana.collectors;

import com.github.games647.minefana.common.AnalyticsType;
import com.github.games647.minefana.common.InfluxConnector;
import com.github.games647.minefana.common.collectors.AbstractCollector;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ForgeCollector extends AbstractCollector {

    public ForgeCollector(InfluxConnector connector) {
        super(connector);
    }

    @Override
    public void run() {
        int forgeUsers = (int) ProxyServer.getInstance().getPlayers().stream()
                .filter(ProxiedPlayer::isForgeUser)
                .count();

        send(AnalyticsType.FORGE_USER.newPoint().addField("users", forgeUsers));
        addFields(AnalyticsType.FORGE_MODS, ProxyServer.getInstance().getPlayers().stream()
                .map(ProxiedPlayer::getModList)
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )));
    }
}
