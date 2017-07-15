package com.github.games647.minefana.common;

import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

public class InfluxConnector implements Closeable {

    private final String url;
    private final String username;
    private final String password;
    private final String database;

    private InfluxDB connection;

    public InfluxConnector(String url, String username, String password, String database) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public void init() {
        InfluxDB influxDB = InfluxDBFactory.connect(url, username, password);
        influxDB.createDatabase(username);

        // Flush every 2000 Points, at least every 100ms
        influxDB.enableBatch(2_000, 100, TimeUnit.MILLISECONDS);
        influxDB.enableGzip();

        connection = influxDB;
    }

    public void send(Point measurement) {
        send(Arrays.asList(measurement));
    }

    public void send(List<Point> measurements) {
        BatchPoints batchPoints = BatchPoints.database(database).retentionPolicy("autogen").build();
        measurements.stream().forEach(batchPoints::point);
        connection.write(batchPoints);
    }

    @Override
    public void close() {
        if (connection != null) {
            connection.close();
        }
    }
}
