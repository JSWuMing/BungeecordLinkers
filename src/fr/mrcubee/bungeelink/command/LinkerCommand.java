package fr.mrcubee.bungeelink.command;

import com.mysql.jdbc.StringUtils;
import fr.mrcubee.bungeelink.BungeeCordLinkers;
import fr.mrcubee.bungeelink.command.util.KeyStatusBuilder;
import fr.mrcubee.bungeelink.security.KeyManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

public class LinkerCommand extends Command {

    private final BungeeCordLinkers bungeeCordLinkers;

    public LinkerCommand(BungeeCordLinkers bungeeCordLinkers) {
        super("link", "bungeecordlinkers.command.link");
        this.bungeeCordLinkers = bungeeCordLinkers;
    }

    private void commandLoadKey(CommandSender commandSender, String keyName, String fileName) {
        File keyFile;

        if (commandSender == null || StringUtils.isEmptyOrWhitespaceOnly(keyName) || StringUtils.isEmptyOrWhitespaceOnly(fileName))
            return;
        keyFile = new File(this.bungeeCordLinkers.getKeyFolder(), fileName);
        if (!keyFile.exists() || keyFile.isDirectory()) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "The key cannot be found."));
            return;
        }
        if (this.bungeeCordLinkers.getKeyManager().getKey(keyName) != null) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "This name is already in use."));
            return;
        }
        if (this.bungeeCordLinkers.getKeyManager().register(keyName, fileName))
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN  + "The key is registered."));
        else
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED  + "Unable to load public key."));
    }

    private void commandKeyAction(CommandSender commandSender, String target, String action) {
        if (commandSender == null || action == null)
            return;
        switch (action) {
            case "enable":
                if (this.bungeeCordLinkers.getKeyManager().setDisable(target, false))
                    commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "The key enabled."));
                else
                    commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + " The key is already enabled."));
                break;
            case "disable":
                if (this.bungeeCordLinkers.getKeyManager().setDisable(target, true))
                    commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "The key disabled."));
                else
                    commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "The key is already disabled."));
                break;
            case "remove":
                if (this.bungeeCordLinkers.getKeyManager().unRegister(target))
                    commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "The key deleted."));
                else
                    commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Error while deleting the key.."));
                break;
        }
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        KeyManager keyManager = this.bungeeCordLinkers.getKeyManager();
        Set<String> names = keyManager.getKeyNames();;
        int count = (names == null) ? 0 : names.size();

        if (args == null || args.length < 1) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD.toString() + ChatColor.BOLD + "KEYS (" + count + "):"));
            if (names == null)
                return;
            for (String name : names)
                commandSender.sendMessage(KeyStatusBuilder.generateKeyLineStatus(keyManager, name, commandSender instanceof ProxiedPlayer));
            return;
        }
        if (args.length < 2 || !Arrays.asList("load", "enable", "disable", "remove").contains(args[0]))
            return;
        if (args[0].equalsIgnoreCase("load") && args.length > 2) {
            commandLoadKey(commandSender, args[1], args[2]);
            return;
        }
        if (names == null || !names.contains(args[1])) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "This key does not exist."));
            return;
        }
        commandKeyAction(commandSender, args[1], args[0]);
    }
}
