package com.github.games647.minefana.common.collectors;

import com.github.games647.minefana.common.AnalyticsType;
import com.github.games647.minefana.common.InfluxConnector;

import java.util.Collection;

import org.influxdb.dto.Point;

public abstract class WorldCollector<W> extends AbstractCollector {

    public WorldCollector(InfluxConnector connector) {
        super(connector);
    }

    @Override
    public void run() {
        for (W world : getWorlds()) {
            String worldName = getName(world);

            Point.Builder pointBuilder = AnalyticsType.WORLD.newPoint();

            pointBuilder.tag("world", worldName)
                    .addField("players", getPlayers(world))
                    .addField("chunks", getChunks(world))
                    .addField("entities", getEntities(world))
                    .addField("tileEntities", getTileEntities(world));

            send(pointBuilder);
        }
    }

    protected abstract Collection<W> getWorlds();

    protected abstract String getName(W world);

    protected abstract int getChunks(W world);
    protected abstract int getPlayers(W world);
    protected abstract int getEntities(W world);
    protected abstract int getTileEntities(W world);
}
