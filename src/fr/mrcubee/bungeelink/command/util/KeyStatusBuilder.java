package fr.mrcubee.bungeelink.command.util;

import com.mysql.jdbc.StringUtils;
import fr.mrcubee.bungeelink.security.KeyManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class KeyStatusBuilder {

    private static BaseComponent generateRemoveButton(String keyName) {
        TextComponent result;

        if (StringUtils.isEmptyOrWhitespaceOnly(keyName))
            return null;
        result = new TextComponent();
        result.setText("[REMOVE]");
        result.setColor(ChatColor.RED);
        result.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/link remove " + keyName));
        return result;
    }

    private static BaseComponent generateEnableButton(String keyName, boolean disable) {
        TextComponent result;

        if (StringUtils.isEmptyOrWhitespaceOnly(keyName))
            return null;
        result = new TextComponent();
        if (disable) {
            result.setText("[ENABLE] ");
            result.setColor(ChatColor.GREEN);
            result.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/link enable " + keyName));
        } else {
            result.setText("[DISABLE] ");
            result.setColor(ChatColor.RED);
            result.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/link disable " + keyName));
        }
        result.addExtra(generateRemoveButton(keyName));
        return result;
    }

    public static BaseComponent generateKeyLineStatus(KeyManager keyManager, String keyName, boolean button) {
        TextComponent result;
        boolean disable;

        if (keyManager == null || StringUtils.isEmptyOrWhitespaceOnly(keyName))
            return null;
        disable = keyManager.isDisabled(keyName);
        result = new TextComponent();
        result.setText("  " + keyName + " ");
        result.setColor(disable ? ChatColor.RED : ChatColor.GREEN);
        if (button)
            result.addExtra(generateEnableButton(keyName, disable));
        return result;
    }

}
