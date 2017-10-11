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
        Point.Builder playersBuilder = AnalyticsType.WORLD_PLAYERS.newPoint();
        Point.Builder chunksBuilder = AnalyticsType.WORLD_CHUNKS.newPoint();
        Point.Builder entityBuilder = AnalyticsType.WORLD_ENTITIES.newPoint();
        Point.Builder tileBuilder = AnalyticsType.WORLD_TILE_ENTITIES.newPoint();
        for (W world : getWorlds()) {
            String worldName = getName(world);

            playersBuilder.addField(worldName, getPlayers(world));
            chunksBuilder.addField(worldName, getChunks(world));
            entityBuilder.addField(worldName, getEntities(world));
            tileBuilder.addField(worldName, getTileEntities(world));
        }

        send(playersBuilder);
        send(chunksBuilder);
        send(entityBuilder);
        send(tileBuilder);
    }

    protected abstract Collection<W> getWorlds();

    protected abstract String getName(W world);

    protected abstract int getChunks(W world);
    protected abstract int getPlayers(W world);
    protected abstract int getEntities(W world);
    protected abstract int getTileEntities(W world);
}
