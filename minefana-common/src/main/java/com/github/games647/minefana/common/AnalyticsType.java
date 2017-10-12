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

    DAILY_PLAYTIME, //this includes daily average and session average
    USERS, //returning users and new users

    FORGE_USER,
    FORGE_MODS,

    LOCALE,
    PING,
    COUNTRY;

    public Point.Builder newPoint() {
        return Point.measurement(name());
    }
}
