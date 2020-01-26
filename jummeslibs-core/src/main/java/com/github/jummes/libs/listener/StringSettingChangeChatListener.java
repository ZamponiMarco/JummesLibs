package com.github.jummes.libs.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.setting.StringFieldChangeInventoryHolder;
import com.github.jummes.libs.gui.setting.StringFieldChangeInventoryHolder.StringFieldChangeInfo;
import com.github.jummes.libs.util.MessageUtils;

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
		StringFieldChangeInfo changeStringInfo = StringFieldChangeInventoryHolder.getChangeStringInfoSet().stream()
				.filter(info -> info.getHuman().equals(p)).findFirst().orElse(null);
		if (changeStringInfo != null) {
			runModifySyncTask(p, e.getMessage(), changeStringInfo);
			e.setCancelled(true);
		}
	}

	private void runModifySyncTask(Player p, String message, StringFieldChangeInfo info) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			String validatedValue = message.trim();
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
