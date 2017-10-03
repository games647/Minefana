package com.github.games647.minefana.common;

public enum MeasurementType {

    TPS,

    PLAYERS,

    PROTOCOL_VERSION,

    COUNTRY;

    public String getId() {
        return name().toLowerCase();
    }
}
