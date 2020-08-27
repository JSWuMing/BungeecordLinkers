package fr.mrcubee.bungeelink.listeners;

import fr.mrcubee.bungeelink.BungeeCordLinkers;
import fr.mrcubee.bungeelink.player.ConnectionData;
import fr.mrcubee.bungeelink.player.ConnectionDataBuilder;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;

public class ServerConnect implements Listener {

    private final BungeeCordLinkers bungeeCordLinker;

    protected ServerConnect(BungeeCordLinkers bungeeCordLinker) {
        this.bungeeCordLinker = bungeeCordLinker;
    }

    @EventHandler
    public void serverConnect(ServerConnectEvent event) {
        ConnectionData connectionData = this.bungeeCordLinker.getConnectionManager().get(event.getPlayer().getUniqueId());

        if (connectionData == null)
            connectionData = ConnectionDataBuilder.buildFrom(event.getPlayer(), this.bungeeCordLinker.getKeyManager().getPrivateKey());
        ((InitialHandler) event.getPlayer().getPendingConnection()).getHandshake().setHost(connectionData.toString());
    }

}
