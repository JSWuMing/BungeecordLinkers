package fr.mrcubee.bungeelink.player;

import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

public class ConnectionManager {

    private final HashMap<UUID, ConnectionData> connections;

    public ConnectionManager() {
        this.connections = new HashMap<UUID, ConnectionData>();
    }

    public boolean register(UUID uuid, ConnectionData connectionData) {
        if (uuid == null || connectionData == null || this.connections.containsKey(uuid))
            return false;
        this.connections.put(uuid, connectionData);
        return true;
    }

    public boolean unRegister(UUID uuid) {
        return uuid != null && this.connections.remove(uuid) != null;
    }

    public ConnectionData get(UUID uuid) {
        if (uuid == null)
            return null;
        return this.connections.get(uuid);
    }

}
