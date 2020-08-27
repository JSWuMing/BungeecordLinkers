package fr.mrcubee.bungeelink.api.event;

import fr.mrcubee.bungeelink.player.ConnectionData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.connection.InitialHandler;

import java.util.Arrays;

public class PlayerLoginFromLinkerEvent extends Event implements Cancellable {

    private boolean cancelled;
    private BaseComponent[] cancelReasonComponents;
    private final InitialHandler connection;
    private final ConnectionData connectionData;
    private final String keyUsed;

    public PlayerLoginFromLinkerEvent(InitialHandler connection, ConnectionData connectionData, String keyUsed) {
        this.connection = connection;
        this.connectionData = connectionData;
        this.keyUsed = keyUsed;
        this.cancelReasonComponents = null;
        this.cancelled = false;
    }

    public InitialHandler getConnection() {
        return connection;
    }

    public ConnectionData getConnectionData() {
        return connectionData;
    }

    public BaseComponent[] getCancelReasonComponents() {
        return cancelReasonComponents;
    }

    public void setCancelReason(BaseComponent[] cancelReasonComponents) {
        this.cancelReasonComponents = cancelReasonComponents;
    }

    public String getKeyUsed() {
        return keyUsed;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
