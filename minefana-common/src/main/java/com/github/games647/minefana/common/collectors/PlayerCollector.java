package com.github.games647.minefana.common.collectors;

import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.AnalyticsType;
import com.github.games647.minefana.common.ProtocolVersion;
import com.maxmind.geoip2.record.Country;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class PlayerCollector<P> extends AbstractCollector {

    private final AnalyticsCore core;

    public PlayerCollector(AnalyticsCore core) {
        super(core.getConnector());

        this.core = core;
    }

    @Override
    public void run() {
        Collection<InetAddress> addresses = new ArrayList<>();
        List<String> locales = new ArrayList<>();
        List<ProtocolVersion> protocols = new ArrayList<>();
        for (P player : getOnlinePlayers()) {
            addresses.add(getAddress(player));
            locales.add(getLocale(player));
            protocols.add(getProtocol(player));
        }

        addFields(AnalyticsType.PROTOCOL, protocols.stream()
                .filter(Objects::nonNull)
                .map(ProtocolVersion::name)
                .collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )));

        addFields(AnalyticsType.LOCALE, locales.stream().collect(Collectors.groupingBy(
                Function.identity(), Collectors.counting()
        )));

        addFields(AnalyticsType.COUNTRY, addresses.stream()
                .map(address -> core.getCountry(address).map(Country::getName).orElse("Unknown"))
                .collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )));
    }

    protected abstract Collection<? extends P> getOnlinePlayers();

    protected abstract String getLocale(P player);

    protected abstract InetAddress getAddress(P player);

    protected abstract ProtocolVersion getProtocol(P player);
}
