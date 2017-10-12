package com.github.games647.minefana.common.collectors;

import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.AnalyticsPlayer;
import com.github.games647.minefana.common.AnalyticsType;
import com.github.games647.minefana.common.ProtocolVersion;
import com.maxmind.geoip2.record.Country;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class PlayerCollector<P, T extends AnalyticsPlayer> extends AbstractCollector {

    protected final Map<UUID, T> players = new HashMap<>();
    private final AnalyticsCore core;

    public PlayerCollector(AnalyticsCore core) {
        super(core.getConnector());
        this.core = core;
    }

    @Override
    public void run() {
        addFields(AnalyticsType.PROTOCOL, players.values().stream()
                .map(AnalyticsPlayer::getProtocol)
                .filter(Objects::nonNull)
                .map(ProtocolVersion::name)
                .collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )));

        addFields(AnalyticsType.LOCALE, players.values().stream()
                .map(AnalyticsPlayer::getLocale)
                .collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )));

        addFields(AnalyticsType.COUNTRY, players.values().stream()
                .map(AnalyticsPlayer::getAddress)
                .map(core::getCountry)
                .map(country -> country.map(Country::getName).orElse("Unknown"))
                .collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )));

        send(AnalyticsType.PLAYERS.newPoint()
                .addField("online", players.size())
                .addField("max", getMaxPlayers()));
    }

    protected abstract String getLocale(P player);

    protected abstract InetAddress getAddress(P player);

    protected abstract ProtocolVersion getProtocol(P player);

    protected abstract UUID getUUID(P player);

    protected abstract int getMaxPlayers();

    public abstract void onPlayerJoin(P player);

    public void onPlayerQuit(P player) {
        players.remove(getUUID(player));
    }
}
