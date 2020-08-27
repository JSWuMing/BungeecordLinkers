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
                new PlayerDisconnect(bungeeCordLinker),
                new PlayerLoginFromLinker(bungeeCordLinker),
                new PluginMessage(),
                new PreLogin(bungeeCordLinker),
                new ServerConnect(bungeeCordLinker)
        };
        for (Listener listener : listeners)
            pluginManager.registerListener(bungeeCordLinker, listener);
    }

}
