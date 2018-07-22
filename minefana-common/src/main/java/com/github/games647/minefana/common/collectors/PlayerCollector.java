package com.github.games647.minefana.common.collectors;

import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.AnalyticsType;
import com.github.games647.minefana.common.model.AnalyticsPlayer;
import com.github.games647.minefana.common.model.Country;
import com.github.games647.minefana.common.model.ProtocolVersion;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public abstract class PlayerCollector<P, T extends AnalyticsPlayer> extends AbstractCollector {

    private int newPlayers;

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
                .collect(groupingBy(
                        identity(), counting()
                )));

        addFields(AnalyticsType.LOCALE, players.values().stream()
                .map(AnalyticsPlayer::getLocale)
                .collect(groupingBy(
                        identity(), counting()
                )));

        addFields(AnalyticsType.COUNTRY, players.values().stream()
                .map(AnalyticsPlayer::getAddress)
                .map(core::getCountry)
                .map(country -> country.map(Country::getName).orElse("Unknown"))
                .collect(groupingBy(
                        identity(), counting()
                )));

        send(AnalyticsType.PLAYERS.newPoint()
                .addField("online", players.size())
                .addField("max", getMaxPlayers()));

        send(AnalyticsType.USERS.newPoint().addField("new", newPlayers));
        newPlayers = 0;
    }

    protected abstract String getLocale(P player);

    protected abstract InetAddress getAddress(P player);

    protected abstract ProtocolVersion getProtocol(P player);

    protected abstract boolean isNew(P player);

    protected abstract UUID getUUID(P player);

    protected abstract int getMaxPlayers();

    public void onPlayerJoin(P player) {
        if (isNew(player)) {
            newPlayers++;
        }

        UUID uuid = getUUID(player);

        String locale = getLocale(player);
        InetAddress address = getAddress(player);
        ProtocolVersion protocol = getProtocol(player);
        players.put(uuid, newAnalyticsPlayer(player, locale, address, protocol));
    }

    public void onPlayerQuit(P player) {
        players.remove(getUUID(player));
    }

    protected abstract T newAnalyticsPlayer(P player, String locale, InetAddress address, ProtocolVersion protocol);
}
