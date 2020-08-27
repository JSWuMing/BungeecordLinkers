package fr.mrcubee.bungeelink.listeners;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PluginMessage implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void pluginMessageEvent(PluginMessageEvent event) {
        event.setCancelled(true);
    }

}
