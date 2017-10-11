package com.github.games647.minefana.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ProtocolVersion {

    UNKNOWN(-1),
    MC_1_12_2(340),
    MC_1_12_1(338),
    MC_1_12(335),
    MC_1_11_2(316),
    MC_1_11(315),
    MC_1_10(210),
    MC_1_9_4(110),
    MC_1_9_2(109),
    MC_1_9_1(108),
    MC_1_9(107),
    MC_1_8(47);

    private static final Map<Integer, ProtocolVersion> byId = new HashMap<>();

    static {
        Arrays.stream(ProtocolVersion.values())
                .forEach(version -> byId.put(version.protocol, version));
    }

    private final int protocol;

    ProtocolVersion(int protocol) {
        this.protocol = protocol;
    }

    public static ProtocolVersion getVersion(int protocol) {
        return byId.getOrDefault(protocol, UNKNOWN);
    }

    public int getProtocol() {
        return protocol;
    }
}
