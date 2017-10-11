package com.github.games647.minefana.common;

import org.influxdb.dto.Point;

public enum AnalyticsType {

    TPS,
    PLAYERS,
    PROTOCOL,

    BUNGEE_PLAYER_PER_SERVER,

    WORLD_PLAYERS,
    WORLD_CHUNKS,
    WORLD_ENTITIES,
    WORLD_TILE_ENTITIES,

    TOTAL_CONNECTIONS,
    UNIQUE_CONNECTIONS,

    SESSION_PLAYTIME,
    DAILY_PLAYTIME,

    NEW_USER,
    RETURNING_PLAYERS,

    FORGE_USER,
    FORGE_MODS,

    LOCALE,
    PING,
    COUNTRY;

    public Point.Builder newPoint() {
        return Point.measurement(name());
    }
}
