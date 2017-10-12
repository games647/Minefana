package com.github.games647.minefana;

import com.github.games647.minefana.common.AnalyticsPlayer;
import com.github.games647.minefana.common.ProtocolVersion;

import java.net.InetAddress;
import java.util.Set;

public class BungeeAnalyticsPlayer extends AnalyticsPlayer {

    private final boolean isForgeUser;
    private final Set<String> mods;

    private String currentServer;

    public BungeeAnalyticsPlayer(String locale, InetAddress address, ProtocolVersion protocol
            , boolean isForgeUser, Set<String> mods) {
        super(locale, address, protocol);
        this.isForgeUser = isForgeUser;
        this.mods = mods;
    }

    public boolean isForgeUser() {
        return isForgeUser;
    }

    public Set<String> getMods() {
        return mods;
    }

    public String getCurrentServer() {
        return currentServer;
    }

    public void setCurrentServer(String currentServer) {
        this.currentServer = currentServer;
    }
}
