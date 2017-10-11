package com.github.games647.minefana.common.collectors;

import com.github.games647.minefana.common.AnalyticsType;
import com.github.games647.minefana.common.InfluxConnector;

import java.util.function.DoubleSupplier;

public class PingCollector extends AbstractCollector {

    private final DoubleSupplier collector;

    public PingCollector(InfluxConnector connector, DoubleSupplier collector) {
        super(connector);

        this.collector = collector;
    }

    @Override
    public void run() {
        double avgPing = round(collector.getAsDouble());
        send(AnalyticsType.PING.newPoint().addField("ping", avgPing));
    }
}
