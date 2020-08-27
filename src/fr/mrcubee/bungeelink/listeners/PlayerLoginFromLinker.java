package fr.mrcubee.bungeelink.listeners;

import com.google.gson.GsonBuilder;
import fr.mrcubee.bungeelink.BungeeCordLinkers;
import fr.mrcubee.bungeelink.api.event.PlayerLoginFromLinkerEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerLoginFromLinker implements Listener {

    private final BungeeCordLinkers bungeeCordLinker;

    protected PlayerLoginFromLinker(BungeeCordLinkers bungeeCordLinker) {
        this.bungeeCordLinker = bungeeCordLinker;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerLoginFromLinker(PlayerLoginFromLinkerEvent event) {
        String keyUsed;

        this.bungeeCordLinker.getLogger().info("[" + event.getConnection().getName() + "] <-> Connection from partner.\n"
                + new GsonBuilder().setPrettyPrinting().create().toJson(event.getConnectionData().toJson()));
        keyUsed = event.getKeyUsed();
        if (!event.isCancelled()) {
            if (keyUsed == null)
                keyUsed = "???";
            this.bungeeCordLinker.getLogger().info("[" + event.getConnection().getName() + "] <-> Connection Accepted. (player connection from " + keyUsed + ")");
            return;
        }
        if (this.bungeeCordLinker.getKeyManager().isDisabled(keyUsed)) {
            this.bungeeCordLinker.getLogger().warning("[" + event.getConnection().getName() + "] <-> Connection Disabled. (player connection from " + keyUsed + ")");
            return;
        }
        this.bungeeCordLinker.getLogger().warning("[" + event.getConnection().getName() + "] <-> Connection Refused.");
    }

}
