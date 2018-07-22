package com.github.games647.minefana.collectors;

import com.github.games647.minefana.BungeeAnalyticsPlayer;
import com.github.games647.minefana.MinefanaBungee;
import com.github.games647.minefana.common.AnalyticsCore;
import com.github.games647.minefana.common.AnalyticsType;
import com.github.games647.minefana.common.model.ProtocolVersion;
import com.github.games647.minefana.common.collectors.PlayerCollector;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class BungeePlayerCollector extends PlayerCollector<ProxiedPlayer, BungeeAnalyticsPlayer> {

    private MinefanaBungee plugin;

    public BungeePlayerCollector(AnalyticsCore core, MinefanaBungee plugin) {
        super(core);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        super.run();

        int forgeUsers = (int) players.values().stream().filter(BungeeAnalyticsPlayer::isForgeUser).count();
        send(AnalyticsType.FORGE_USER.newPoint().addField("users", forgeUsers));

        addFields(AnalyticsType.FORGE_MODS, players.values().stream()
                .map(BungeeAnalyticsPlayer::getMods)
                .flatMap(Collection::stream)
                .collect(groupingBy(
                        identity(), counting()
                )));

        addFields(AnalyticsType.BUNGEE_PLAYER_PER_SERVER, players.values().stream()
                .map(BungeeAnalyticsPlayer::getCurrentServer)
                .filter(Objects::nonNull)
                .collect(groupingBy(
                        identity(), counting()
                )));
    }

    @Override
    protected String getLocale(ProxiedPlayer player) {
        String locale;
        try{
            locale = player.getLocale().getDisplayName();
        }catch(Exception e){
            plugin.getLog().error("Error getting locale",e);
            locale = "Error";
        }

        return locale;
    }

    @Override
    protected InetAddress getAddress(ProxiedPlayer player) {
        return player.getAddress().getAddress();
    }

    @Override
    protected boolean isNew(ProxiedPlayer player) {
        return false;
    }

    @Override
    protected ProtocolVersion getProtocol(ProxiedPlayer player) {
        return ProtocolVersion.getVersion(player.getPendingConnection().getVersion());
    }

    @Override
    protected UUID getUUID(ProxiedPlayer player) {
        return player.getUniqueId();
    }

    @Override
    protected int getMaxPlayers() {
        return ProxyServer.getInstance().getConfig().getPlayerLimit();
    }

    public void onServerSwitch(ServerSwitchEvent switchEvent) {
        ProxiedPlayer player = switchEvent.getPlayer();
        BungeeAnalyticsPlayer bungeeAnalyticsPlayer = players.get(player.getUniqueId());
        if (bungeeAnalyticsPlayer != null) {
            String currentServer = getCurrentServer(player);
            bungeeAnalyticsPlayer.setCurrentServer(currentServer);
        }
    }

    private String getCurrentServer(ProxiedPlayer player) {
        String serverName;
        try {
            serverName = player.getServer().getInfo().getName();

        }catch (Exception e){
            plugin.getLog().error("Error getting server name",e);
            serverName = "Error";
        }
        return serverName;
    }

    @Override
    protected BungeeAnalyticsPlayer newAnalyticsPlayer(ProxiedPlayer player, String locale, InetAddress address,
                                                       ProtocolVersion protocol) {
        boolean forgeUser = player.isForgeUser();
        Set<String> mods = player.getModList().keySet();

        BungeeAnalyticsPlayer model = new BungeeAnalyticsPlayer(locale, address, protocol, forgeUser, mods);
        model.setCurrentServer(getCurrentServer(player));
        return model;
    }
}
