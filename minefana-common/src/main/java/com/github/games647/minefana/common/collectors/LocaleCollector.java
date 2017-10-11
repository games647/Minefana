package com.github.games647.minefana.common.collectors;

import com.github.games647.minefana.common.InfluxConnector;

import java.util.Collection;
import java.util.function.Supplier;

public class LocaleCollector extends AbstractCollector {

    private final Supplier<Collection<String>> collector;

    public LocaleCollector(InfluxConnector connector, Supplier<Collection<String>> collector) {
        super(connector);
        this.collector = collector;
    }

    @Override
    public void run() {

    }
}
