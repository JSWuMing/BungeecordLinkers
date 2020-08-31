package fr.mrcubee.bungeelink.listeners;

import fr.mrcubee.bungeelink.BungeeCordLinkers;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.PluginManager;

public class RegisterListeners {

    public static void register(BungeeCordLinkers bungeeCordLinker) {
        PluginManager pluginManager;
        Listener[] listeners;

        if (bungeeCordLinker == null)
            return;
        pluginManager = bungeeCordLinker.getProxy().getPluginManager();
        listeners = new Listener[] {
                new PlayerDisconnectListener(bungeeCordLinker),
                new PlayerLoginFromLinkerListener(bungeeCordLinker),
                new PluginMessageListener(bungeeCordLinker),
                new PreLoginListener(bungeeCordLinker),
                new ServerConnectListener(bungeeCordLinker)
        };
        for (Listener listener : listeners)
            pluginManager.registerListener(bungeeCordLinker, listener);
    }

}
