package fr.mrcubee.bungeelink.listeners;

import fr.mrcubee.bungeelink.BungeeCordLinkers;
import fr.mrcubee.bungeelink.api.event.PlayerLoginFromLinkerEvent;
import fr.mrcubee.bungeelink.player.ConnectionData;
import fr.mrcubee.bungeelink.player.ConnectionDataBuilder;
import fr.mrcubee.bungeelink.player.ConnectionDataUtils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PreLogin implements Listener {

    private final BungeeCordLinkers bungeeCordLinker;

    protected PreLogin(BungeeCordLinkers bungeeCordLinker) {
        this.bungeeCordLinker = bungeeCordLinker;
    }

    private String getKeyUsed(ConnectionData connectionData) {
        String keyUsed;
        String signature;

        if (connectionData == null)
            return null;
        signature = connectionData.getSignature();
        connectionData.setSignature(null);
        keyUsed = this.bungeeCordLinker.getKeyManager().getSignatureOwner(signature.getBytes(),
                connectionData.toString().getBytes());
        connectionData.setSignature(signature);
        return keyUsed;
    }

    private PlayerLoginFromLinkerEvent login(InitialHandler pendingConnection, ConnectionData connectionData) {
        PlayerLoginFromLinkerEvent event;

        if (pendingConnection == null || connectionData == null || !connectionData.isComplete())
            return null;
        event = new PlayerLoginFromLinkerEvent(pendingConnection, connectionData, getKeyUsed(connectionData));
        if (event.getKeyUsed() == null) {
            event.setCancelReason(TextComponent.fromLegacyText(ChatColor.RED + "Invalid signature."));
            event.setCancelled(true);
        } else if (this.bungeeCordLinker.getKeyManager().isDisabled(event.getKeyUsed())) {
            event.setCancelReason(TextComponent.fromLegacyText(ChatColor.RED + "Your signature is disabled."));
            event.setCancelled(true);
        }
        return event;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void preLoginEvent(PreLoginEvent event) {
        InitialHandler initialHandler = (InitialHandler) event.getConnection();
        ConnectionData connectionData = ConnectionDataBuilder.buildFrom(initialHandler);
        PlayerLoginFromLinkerEvent playerLoginFromLinkerEvent;

        if (!BungeeCord.getInstance().getConfig().isOnlineMode())
            return;
        playerLoginFromLinkerEvent = login(initialHandler, connectionData);
        if (playerLoginFromLinkerEvent == null)
            return;
        this.bungeeCordLinker.getProxy().getPluginManager().callEvent(playerLoginFromLinkerEvent);
        event.setCancelled(playerLoginFromLinkerEvent.isCancelled());
        event.setCancelReason(playerLoginFromLinkerEvent.getCancelReasonComponents());
        if (!playerLoginFromLinkerEvent.isCancelled()) {
            this.bungeeCordLinker.getConnectionManager().register(connectionData.getUuid(), connectionData);
            ConnectionDataUtils.applyPlayerData(initialHandler, connectionData);
        }
    }
}
