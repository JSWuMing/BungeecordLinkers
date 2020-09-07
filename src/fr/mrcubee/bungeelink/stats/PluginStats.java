package fr.mrcubee.bungeelink.stats;

import com.mysql.jdbc.StringUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;


public class PluginStats implements Runnable {

    private Plugin plugin;
    private String server_address;
    private int server_port;

    private PluginStats(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        URL url;
        HttpURLConnection httpURLConnection;
        BufferedWriter bufferedWriter;
        InputStream inputStream;
        Optional<ProxiedPlayer> optionalProxiedPlayer = this.plugin.getProxy().getPlayers().stream().findAny();
        ProxiedPlayer proxiedPlayer = optionalProxiedPlayer.orElse(null);
        int players;

        if (proxiedPlayer == null && StringUtils.isEmptyOrWhitespaceOnly(server_address))
            return;
        else if (StringUtils.isEmptyOrWhitespaceOnly(server_address) || server_port <= 0) {
            server_address = proxiedPlayer.getPendingConnection().getVirtualHost().getHostString();
            server_port = proxiedPlayer.getPendingConnection().getVirtualHost().getPort();
        }
        players = this.plugin.getProxy().getPlayers().size();
        try {
            url = new URL("http://mrcubee.fr/spigot/plugin/stats/update/");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
            bufferedWriter.write(String.format("plugin=%s&server_address=%s&server_port=%d&players=%d&whitelist=%b",
                    plugin.getDescription().getName(),
                    server_address,
                    server_port,
                    players,
                    false));
            bufferedWriter.close();
            inputStream = httpURLConnection.getInputStream();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PluginStats createNew(Plugin plugin) {
        return ((plugin != null) ? new PluginStats(plugin) : null);
    }
}
