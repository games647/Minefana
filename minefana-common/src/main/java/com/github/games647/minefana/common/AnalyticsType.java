package com.github.games647.minefana.common;

import org.influxdb.dto.Point;

public enum AnalyticsType {

    TPS,
    PLAYERS,
    PROTOCOL,

    TOTAL_CONNECTIONS,
    UNIQUE_CONNECTIONS,

    SESSION_PLAYTIME,
    DAILY_PLAYTIME,

    NEW_USER,
    RETURNING_PLAYERS,

    FORGE_USERS,
    FORGE_MODS,

    LOCALE,
    PING,
    COUNTRY;

    public Point.Builder newPoint() {
        return Point.measurement(name());
    }
}
