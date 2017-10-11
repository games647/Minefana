package com.github.games647.minefana.common.collectors;

import com.github.games647.minefana.common.InfluxConnector;

import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;

public abstract class AbstractCollector implements Runnable {

    private final InfluxConnector connector;

    public AbstractCollector(InfluxConnector connector) {
        this.connector = connector;
    }

    protected void send(Point.Builder point) {
        connector.send(point
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build());
    }

    protected double round(double rawDouble) {
        return Math.round(rawDouble * 100) / 100.0D;
    }
}
