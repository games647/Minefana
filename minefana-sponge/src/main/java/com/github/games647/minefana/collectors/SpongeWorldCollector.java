package com.github.games647.minefana.collectors;

import com.github.games647.minefana.common.InfluxConnector;
import com.github.games647.minefana.common.collectors.WorldCollector;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import java.util.Collection;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

public class SpongeWorldCollector extends WorldCollector<World> {

    @Inject
    SpongeWorldCollector(InfluxConnector connector) {
        super(connector);
    }

    @Override
    protected Collection<World> getWorlds() {
        return Sponge.getServer().getWorlds();
    }

    @Override
    protected String getName(World world) {
        return world.getName();
    }

    @Override
    protected int getChunks(World world) {
        return Iterables.size(world.getLoadedChunks());
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
        return world.getTileEntities().size();
    }
}
