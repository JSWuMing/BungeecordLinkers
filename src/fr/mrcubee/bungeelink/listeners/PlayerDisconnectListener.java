package fr.mrcubee.bungeelink.listeners;

import fr.mrcubee.bungeelink.BungeeCordLinkers;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerDisconnectListener implements Listener {

    private BungeeCordLinkers bungeeCordLinker;

    protected PlayerDisconnectListener(BungeeCordLinkers bungeeCordLinker) {
        this.bungeeCordLinker = bungeeCordLinker;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDisconnect(PlayerDisconnectEvent event) {
        this.bungeeCordLinker.getConnectionManager().unRegister(event.getPlayer().getUniqueId());
    }
}
