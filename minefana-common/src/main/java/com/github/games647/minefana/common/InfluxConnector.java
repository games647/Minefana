package com.github.games647.minefana.common;

import java.io.Closeable;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.influxdb.BatchOptions;
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

    protected void init() {
        InfluxDB influxDB = InfluxDBFactory.connect(url, username, password);

        if (!influxDB.databaseExists(database)) {
            influxDB.createDatabase(database);
        }

        // Flush every 2000 Points, at least every 1s
        influxDB.enableBatch(2_000, 2, TimeUnit.MINUTES);
        //influxDB.enableBatch(BatchOptions.DEFAULTS.jitterDuration(500));


        influxDB.enableGzip();

        connection = influxDB;
    }

    public void send(Point measurement) {
        send(Collections.singletonList(measurement));
    }

    public void send(Iterable<Point> measurements) {
        BatchPoints batchPoints = BatchPoints.database(database).retentionPolicy("autogen").build();
        measurements.forEach(batchPoints::point);
        connection.write(batchPoints);
    }

    @Override
    public void close() {
        if (connection != null) {
            connection.close();
        }
    }
}
