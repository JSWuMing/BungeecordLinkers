package fr.mrcubee.bungeelink.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.mrcubee.bungeelink.security.KeyUtils;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;

import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.UUID;

public class ConnectionDataBuilder {

    public static ConnectionData buildFrom(ProxiedPlayer player) {
        ConnectionData result = new ConnectionData();
        InetSocketAddress host;
        InitialHandler initialHandler;

        if (player == null)
            return null;
        result.userName = player.getName();
        result.uuid = player.getUniqueId();
        host = (InetSocketAddress) player.getSocketAddress();
        result.address = InetSocketAddress.createUnresolved(host.getHostString(), host.getPort());
        host = player.getPendingConnection().getVirtualHost();
        result.virtualHost = InetSocketAddress.createUnresolved(host.getHostString(), host.getPort());
        result.requestRank = null;
        result.targetedServer = null;
        initialHandler = (InitialHandler) player.getPendingConnection();
        if (initialHandler.getLoginProfile() != null && initialHandler.getLoginProfile().getProperties() != null)
            result.properties = ((InitialHandler) player.getPendingConnection()).getLoginProfile().getProperties().clone();
        return result;
    }

    public static ConnectionData buildFrom(ProxiedPlayer player, PrivateKey privateKey) {
        ConnectionData connectionData;
        byte[] signatureBytes;

        if (privateKey == null)
            return null;
        connectionData = buildFrom(player);
        if (connectionData == null)
            return null;
        signatureBytes = KeyUtils.createSignature(privateKey, connectionData.toString().getBytes());
        if (signatureBytes != null)
            connectionData.signature = new String(signatureBytes, 0, 0, signatureBytes.length);
        return connectionData;
    }

    private static void getKeysFromJson(ConnectionData connectionData, JsonObject jsonObject) {
        if (connectionData == null || jsonObject == null)
            return;
        if (jsonObject.has("signature"))
            connectionData.signature = jsonObject.get("signature").getAsString();
    }

    private static void getPlayerFromJson(ConnectionData connectionData, JsonObject jsonObject) {
        String host;
        int port;

        if (connectionData == null || jsonObject == null)
            return;
        if (jsonObject.has("username"))
            connectionData.userName = jsonObject.get("username").getAsString();
        if (jsonObject.has("uuid")) {
            try {
                connectionData.uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
            } catch (Exception ignored) {};
        }
        if (jsonObject.has("ip"))
            connectionData.address = InetSocketAddress.createUnresolved(jsonObject.get("ip").getAsString(), 25565);
        if (jsonObject.has("host") && jsonObject.has("port")) {
            host = jsonObject.get("host").getAsString();
            port = jsonObject.get("port").getAsInt();
            connectionData.virtualHost = InetSocketAddress.createUnresolved(host, port);
        }
    }

    private static void getInstructionsFromJson(ConnectionData connectionData, JsonObject jsonObject) {
        if (connectionData == null || jsonObject == null)
            return;
        if (jsonObject.has("requestRank"))
            connectionData.requestRank = jsonObject.get("requestRank").getAsString();
        if (jsonObject.has("targetedServer"))
            connectionData.targetedServer = jsonObject.get("targetedServer").getAsString();
    }

    private static void getPropertiesFromJson(ConnectionData connectionData, JsonObject jsonObject) {
        JsonArray jsonArray;
        ArrayList<LoginResult.Property> properties;

        if (connectionData == null || jsonObject == null || !jsonObject.has("properties"))
            return;
        jsonArray = jsonObject.getAsJsonArray("properties");
        if (jsonArray == null || jsonArray.size() < 1)
            return;
        properties = new ArrayList<LoginResult.Property>();
        jsonArray.forEach(element -> {
            JsonObject jsonElement;
            LoginResult.Property property;

            if (!(element instanceof JsonObject))
                return;
            jsonElement = (JsonObject) element;
            if (!jsonElement.has("name") || !jsonElement.has("value"))
                return;
            property = new LoginResult.Property(jsonElement.get("name").getAsString(),
                    jsonElement.get("value").getAsString(), jsonElement.get("signature").getAsString());
            properties.add(property);
        });
        if (properties.size() > 0) {
            connectionData.properties = new LoginResult.Property[properties.size()];
            for (int i = 0; i < properties.size(); i++)
                connectionData.properties[i] = properties.get(i);;
        }
    }

    public static ConnectionData buildFrom(PendingConnection pendingConnection) {
        ConnectionData result;
        JsonParser jsonParser;
        JsonElement jsonElement;
        JsonObject jsonObject;

        if (pendingConnection == null)
            return null;
        jsonParser = new JsonParser();
        try {
            jsonElement = jsonParser.parse(pendingConnection.getVirtualHost().getHostName());
        } catch (Exception ignored) {
            return null;
        }
        if (!(jsonElement instanceof JsonObject))
            return null;
        jsonObject = (JsonObject) jsonElement;
        result = new ConnectionData();
        getKeysFromJson(result, jsonObject);
        getPlayerFromJson(result, jsonObject);
        getInstructionsFromJson(result, jsonObject);
        getPropertiesFromJson(result, jsonObject);
        return result;
    }

}
