package fr.mrcubee.bungeelink.listeners;

import fr.mrcubee.bungeelink.BungeeCordLinkers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class PluginMessageListener implements Listener {

    private final ProxyServer proxy;

    protected PluginMessageListener(BungeeCordLinkers bungeeCordLinkers) {
        this.proxy = bungeeCordLinkers.getProxy();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void pluginMessageEvent(PluginMessageEvent event) {
        PluginMessage pluginMessage;
        String tag = event.getTag();
        ByteBuf brand;
        String serverBrand;

        if (tag.equals("minecraft:brand") || tag.equals("MC|Brand")) {
            event.setCancelled(true);
            brand = Unpooled.wrappedBuffer(event.getData());
            serverBrand = DefinedPacket.readString(brand);
            brand.release();
            brand = ByteBufAllocator.DEFAULT.heapBuffer();
            DefinedPacket.writeString(this.proxy.getName() + " (" + this.proxy.getVersion() + ") <- " + serverBrand, brand);
            pluginMessage = new PluginMessage(tag, DefinedPacket.toArray(brand), true);
            brand.release();
            event.getReceiver().unsafe().sendPacket(pluginMessage);
        }
    }

}
