package fr.mrcubee.bungeelink.player;

import com.mysql.jdbc.StringUtils;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;

import java.lang.reflect.Field;

public class ConnectionDataUtils {

    private static void setField(Object obj, String fieldName, Object value) {
        Field field;

        if (obj == null || StringUtils.isEmptyOrWhitespaceOnly(fieldName) || value == null)
            return;
        try {
            field = obj.getClass().getDeclaredField(fieldName);
            if (!field.getType().isInstance(value))
                return;
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
    }

    public static void applyPlayerData(InitialHandler initialHandler, ConnectionData connectionData) {
        LoginResult loginResult;

        if (initialHandler == null || connectionData == null)
            return;
        initialHandler.setOnlineMode(false);
        initialHandler.setUniqueId(connectionData.getUuid());
        if (connectionData.getVirtualHost() != null) {
            initialHandler.getHandshake().setHost(connectionData.getVirtualHost().getHostString());
            initialHandler.getHandshake().setPort(connectionData.getVirtualHost().getPort());
        }
        loginResult = new LoginResult(connectionData.getUuid().toString().replaceAll("-", ""),
                connectionData.getUserName(), connectionData.getProperties());
        setField(initialHandler, "loginProfile", loginResult);
        setField(initialHandler, "virtualHost", connectionData.getVirtualHost());
    }

}
