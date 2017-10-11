package com.github.games647.minefana.common.collectors;

import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.AnalyticsType;
import com.maxmind.geoip2.record.Country;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.influxdb.dto.Point;

public class GeoCollector extends AbstractCollector {

    private final AnalyticsCore core;
    private final Supplier<Collection<InetAddress>> collector;

    public GeoCollector(AnalyticsCore core, Supplier<Collection<InetAddress>> collector) {
        super(core.getConnector());

        this.core = core;
        this.collector = collector;
    }

    @Override
    public void run() {
        Point.Builder countryBuilder = AnalyticsType.COUNTRY.newPoint();

        Map<String, Integer> countries = new HashMap<>();
        for (InetAddress address : collector.get()) {
            String countryName = core.getCountry(address).map(Country::getName).orElse("Unknown");
            Integer count = countries.getOrDefault(countryName, 0);
            countries.put(countryName, count + 1);
        }

        for (Map.Entry<String, Integer> entry : countries.entrySet()) {
            countryBuilder.addField(entry.getKey(), entry.getValue());
        }

        send(countryBuilder);
    }
}
