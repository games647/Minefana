package com.github.games647.minefana.collectors;

import com.github.games647.minefana.common.InfluxConnector;
import com.github.games647.minefana.common.collectors.WorldCollector;

import java.util.Collection;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class BukkitWorldCollector extends WorldCollector<World> {

    public BukkitWorldCollector(InfluxConnector connector) {
        super(connector);
    }

    @Override
    protected Collection<World> getWorlds() {
        return Bukkit.getWorlds();
    }

    @Override
    protected String getName(World world) {
        return world.getName();
    }

    @Override
    protected int getChunks(World world) {
        return world.getLoadedChunks().length;
    }

    @Override
    protected int getPlayers(World world) {
        return world.getPlayers().size();
    }

    @Override
    protected int getEntities(World world) {
        return world.getEntities().size();
    }

    @Override
    protected int getTileEntities(World world) {
        return Stream.of(world.getLoadedChunks())
                .map(Chunk::getTileEntities)
                .mapToInt(value -> value.length)
                .sum();
    }
}
