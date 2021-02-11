package com.github.jummes.libs.listener;

import com.github.jummes.libs.gui.setting.StringFieldChangeInventoryHolder;
import com.github.jummes.libs.gui.setting.StringFieldChangeInventoryHolder.StringFieldChangeInfo;
import com.github.jummes.libs.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public class StringSettingChangeChatListener implements Listener {

    private static final String MODIFY_SUCCESS = MessageUtils.color("&aObject modified, &6%s: &e%s");
    private static final String MODIFY_BLOCKED = MessageUtils.color("&aThe value &6&lhasn't&a been modified.");

    private JavaPlugin plugin;

    public StringSettingChangeChatListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        handleChatMessage(e, p, message);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        handleChatMessage(e, p, message);
    }

    private void handleChatMessage(Cancellable e, Player p, String message) {
        StringFieldChangeInfo changeStringInfo = StringFieldChangeInventoryHolder.getChangeStringInfoSet().stream()
                .filter(info -> info.getHuman().equals(p)).findFirst().orElse(null);
        if (changeStringInfo != null) {
            runModifySyncTask(p, message, changeStringInfo);
            e.setCancelled(true);
        }
    }

    private void runModifySyncTask(Player p, String message, StringFieldChangeInfo info) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            String validatedValue = message.trim();
            validatedValue = MessageUtils.color(validatedValue);
            if (validatedValue.equalsIgnoreCase("exit")) {
                p.sendMessage(MODIFY_BLOCKED);
            } else {
                info.getChangeInformation().setValue(info.getPath(), validatedValue);
                p.sendMessage(String.format(MODIFY_SUCCESS, info.getChangeInformation().getName(), validatedValue));
            }
            openParentInventory(p, info.getParent());
            StringFieldChangeInventoryHolder.getChangeStringInfoSet().remove(info);
        });
    }

    private void openParentInventory(Player p, InventoryHolder parent) {
        if (parent != null) {
            p.openInventory(parent.getInventory());
        } else {
            p.closeInventory();
        }
    }

}
