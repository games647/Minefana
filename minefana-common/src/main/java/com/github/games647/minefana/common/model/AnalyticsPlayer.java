package com.github.games647.minefana.common.model;

import java.net.InetAddress;

public class AnalyticsPlayer {

    private final String locale;
    private final InetAddress address;
    private final ProtocolVersion protocol;

    public AnalyticsPlayer(String locale, InetAddress address, ProtocolVersion protocol) {
        this.locale = locale;
        this.address = address;
        this.protocol = protocol;
    }

    public String getLocale() {
        return locale;
    }

    public InetAddress getAddress() {
        return address;
    }

    public ProtocolVersion getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '{' +
                "locale='" + locale + '\'' +
                ", address=" + address +
                ", protocol=" + protocol +
                '}';
    }
}
