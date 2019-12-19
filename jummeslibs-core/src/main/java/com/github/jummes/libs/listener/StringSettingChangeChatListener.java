package com.github.jummes.libs.listener;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.setting.StringSettingChangeInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.MessageUtils;

public class StringSettingChangeChatListener implements Listener {

	private static final String MODIFY_SUCCESS = MessageUtils.color("&aObject modified, &6%s: &e%s");
	private static final String MODIFY_ERROR = MessageUtils.color("&aThe value &6&lhasn't&a been modified.");
	
	private JavaPlugin plugin;

	public StringSettingChangeChatListener(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Map<HumanEntity, Entry<ModelPath<? extends Model>, Entry<Field, InventoryHolder>>> settingsMap = StringSettingChangeInventoryHolder
				.getSettingsMap();
		Player p = e.getPlayer();
		if (settingsMap != null && settingsMap.get(p) != null) {
			runModifySyncTask(p, e.getMessage(), settingsMap);
			e.setCancelled(true);
		}
	}

	private void runModifySyncTask(Player p, String message,
			Map<HumanEntity, Entry<ModelPath<? extends Model>, Entry<Field, InventoryHolder>>> settingsMap) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			String validatedValue = message.trim();
			if (!validatedValue.equalsIgnoreCase("exit")) {
				ModelPath<?> path = settingsMap.get(p).getKey();
				Field field = settingsMap.get(p).getValue().getKey();
				InventoryHolder parent = settingsMap.get(p).getValue().getValue();
				try {
					field.setAccessible(true);
					field.set(path.getLast(), validatedValue);
					field.setAccessible(false);
					path.updateModel();
					p.sendMessage(
							String.format(MODIFY_SUCCESS, field.getName(), validatedValue));
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (parent != null) {
					p.openInventory(parent.getInventory());
				} else {
					p.closeInventory();
				}
			} else {
				p.sendMessage(MODIFY_ERROR);
			}
			settingsMap.remove(p);
		});
	}

}
