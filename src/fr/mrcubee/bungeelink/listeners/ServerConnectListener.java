package fr.mrcubee.bungeelink.listeners;

import fr.mrcubee.bungeelink.BungeeCordLinkers;
import fr.mrcubee.bungeelink.player.ConnectionData;
import fr.mrcubee.bungeelink.player.ConnectionDataBuilder;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;

import java.security.PrivateKey;

public class ServerConnectListener implements Listener {

    private final BungeeCordLinkers bungeeCordLinker;

    protected ServerConnectListener(BungeeCordLinkers bungeeCordLinker) {
        this.bungeeCordLinker = bungeeCordLinker;
    }

    @EventHandler
    public void serverConnect(ServerConnectEvent event) {
        ConnectionData connectionData = this.bungeeCordLinker.getConnectionManager().get(event.getPlayer().getUniqueId());
        PrivateKey privateKey = this.bungeeCordLinker.getKeyManager().getPrivateKey();

        if (connectionData == null) {
            connectionData = (privateKey == null) ? ConnectionDataBuilder.buildFrom(event.getPlayer())
                    : ConnectionDataBuilder.buildFrom(event.getPlayer(), privateKey);
        }
        if (connectionData == null)
            return;
        ((InitialHandler) event.getPlayer().getPendingConnection()).getHandshake().setHost(connectionData.toString());
    }

}
