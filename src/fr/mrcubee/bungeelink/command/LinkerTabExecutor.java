package fr.mrcubee.bungeelink.command;

import fr.mrcubee.bungeelink.BungeeCordLinkers;
import fr.mrcubee.bungeelink.security.KeyManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class LinkerTabExecutor extends LinkerCommand implements TabExecutor {

    private final BungeeCordLinkers bungeeCordLinkers;

    public LinkerTabExecutor(BungeeCordLinkers bungeeCordLinkers) {
        super(bungeeCordLinkers);
        this.bungeeCordLinkers = bungeeCordLinkers;
    }

    private Set<String> getKeys(boolean enabled) {
        KeyManager keyManager = this.bungeeCordLinkers.getKeyManager();
        Set<String> result = keyManager.getKeyNames();

        result.removeIf(keyName -> keyManager.isDisabled(keyName) == enabled);
        return result;
    }

    private List<String> getFiles() {
        KeyManager keyManager = this.bungeeCordLinkers.getKeyManager();
        List<String> result;
        String[] names = this.bungeeCordLinkers.getKeyFolder().list((dir, name) -> new File(dir, name).isFile()
                && !keyManager.isLoaded(name));

        if (names == null || names.length < 1)
            return null;
        return new ArrayList<String>(Arrays.asList(names));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        List<String> result = null;

        if (args == null || args.length <= 1)
            result = new ArrayList<String>(Arrays.asList("load", "enable", "disable", "remove"));
        else if (args[0].equalsIgnoreCase("enable"))
            result = new ArrayList<String>(getKeys(false));
        else if (args[0].equalsIgnoreCase("disable"))
            result = new ArrayList<String>(getKeys(true));
        else if (args[0].equalsIgnoreCase("remove"))
            result = new ArrayList<String>(this.bungeeCordLinkers.getKeyManager().getKeyNames());
        else if (args[0].equalsIgnoreCase("load") && args.length > 2)
            result = getFiles();
        if (result != null)
            result.removeIf(str -> args != null && args.length > 0 && !str.startsWith(args[args.length - 1]));
        else
            result = new ArrayList<String>();
        return result;
    }
}
