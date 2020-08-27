package fr.mrcubee.bungeelink.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.jdbc.StringUtils;
import fr.mrcubee.bungeelink.security.KeyUtils;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;

import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

public class ConnectionData {

    protected ConnectionData() {

    }

    protected String signature;
    protected String userName;
    protected UUID uuid;
    protected InetSocketAddress address;
    protected InetSocketAddress virtualHost;
    protected String requestRank;
    protected String targetedServer;
    protected LoginResult.Property[] properties;

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getUserName() {
        return this.userName;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    public InetSocketAddress getVirtualHost() {
        return this.virtualHost;
    }

    public void setRequestRank(String requestRank) {
        this.requestRank = requestRank;
    }

    public String getRequestRank() {
        return this.requestRank;
    }

    public void setTargetedServer(String targetedServer) {
        this.targetedServer = targetedServer;
    }

    public String getTargetedServer() {
        return this.targetedServer;
    }

    public void setProperties(LoginResult.Property[] properties) {
        this.properties = properties;
    }

    public LoginResult.Property[] getProperties() {
        return properties;
    }

    public boolean isComplete() {
        return !StringUtils.isNullOrEmpty(this.signature) && !StringUtils.isNullOrEmpty(this.userName)
                && this.uuid != null && this.address != null && this.address.getHostName() != null;
    }

    public JsonElement toJson() {
        return  new JsonParser().parse(toString());
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray;
        JsonObject jsonArrayObject;

        if (!StringUtils.isEmptyOrWhitespaceOnly(this.signature))
            jsonObject.addProperty("signature", this.signature);
        if (!StringUtils.isEmptyOrWhitespaceOnly(this.userName))
            jsonObject.addProperty("username", this.userName);
        if (this.uuid != null)
            jsonObject.addProperty("uuid", this.uuid.toString());
        if (this.address != null && !StringUtils.isEmptyOrWhitespaceOnly(this.address.getHostString()))
            jsonObject.addProperty("ip", this.address.getHostString());
        if (this.virtualHost != null && !StringUtils.isEmptyOrWhitespaceOnly(this.virtualHost.getHostString())) {
            jsonObject.addProperty("host", this.virtualHost.getHostName());
            jsonObject.addProperty("port", this.virtualHost.getPort());
        }
        if (!StringUtils.isEmptyOrWhitespaceOnly(this.requestRank))
            jsonObject.addProperty("requestRank", this.requestRank);
        if (!StringUtils.isEmptyOrWhitespaceOnly(this.targetedServer))
            jsonObject.addProperty("targetedServer", this.targetedServer);
        if (this.properties != null && this.properties.length > 0) {
            jsonArray = new JsonArray();
            for (LoginResult.Property property : this.properties) {
                if (StringUtils.isNullOrEmpty(property.getName()) || StringUtils.isNullOrEmpty(property.getValue()))
                    continue;
                jsonArrayObject = new JsonObject();
                jsonArrayObject.addProperty("name", property.getName());
                jsonArrayObject.addProperty("value", property.getValue());
                if (!StringUtils.isNullOrEmpty(property.getSignature()))
                    jsonArrayObject.addProperty("signature", property.getSignature());
                jsonArray.add(jsonArrayObject);
            }
            jsonObject.add("properties", jsonArray);
        }
        return jsonObject.toString();
    }
}
