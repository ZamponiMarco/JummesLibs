package com.github.jummes.libs.gui.setting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;

public class IntegerFieldChangeInventoryHolder extends FieldChangeInventoryHolder {

	private static final String ARROW_LEFT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjk5ZjA0OTI4OGFjNTExZjZlN2VjNWM5MjM4Zjc2NTI3YzJmYmNhZDI4NTc0MzZhYzM4MTU5NmNjMDJlNCJ9fX0==";
	private static final String ARROW2_LEFT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODZlMTQ1ZTcxMjk1YmNjMDQ4OGU5YmI3ZTZkNjg5NWI3Zjk2OWEzYjViYjdlYjM0YTUyZTkzMmJjODRkZjViIn19fQ===";
	private static final String ARROW3_LEFT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzhhMWYwMzdjOGU1MTc4YmJlNGNiOWE3ZDMzNWYxYjExMGM0NWMxYzQ2NWYxZDczZGNiZThjYWQ2OWQ5ZWNhIn19fQ===";
	private static final String ARROW_RIGHT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY4YWQyNTVmOTJiODM5YjVhOGQxYmJiOWJiNGQxYTVmMzI3NDNiNmNmNTM2NjVkOTllZDczMmFhOGJlNyJ9fX0====";
	private static final String ARROW2_RIGHT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzY5N2MyNDg5MmNmYzAzYzcyOGZmYWVhYmYzNGJkZmI5MmQ0NTExNDdiMjZkMjAzZGNhZmE5M2U0MWZmOSJ9fX0====";
	private static final String ARROW3_RIGHT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGY0NDg2ODYzZjMwZTM4NDMyZGJkMjJlNTQxMjk2NDY0NGVjMjVlYTRmOTkxYTM4YzczNzM3NmU5NjA2NDc5In19fQ=====";
	private static final String ZERO_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmEzMTNhY2E1ZDgzZjk3OWZiODhlNjIwNDNiMzhiNmE1ZTQzZDkyYzk4MzFjZWQ0Njk3MTlmNGMzNzYyMmUxIn19fQ=======";
	private static final String SUBMIT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWE3NWM4ZTUxYzNkMTA1YmFiNGM3ZGUzM2E3NzA5MzczNjRiNWEwMWMxNWI3ZGI4MmNjM2UxZmU2ZWI5MzM5NiJ9fX0======";

	private static final String MENU_TITLE = MessageUtils.color("&6&lModify &e&l %s");
	private static final String MODIFY_SUCCESS = MessageUtils.color("&aObject modified: &6%s: &e%s");
	private static final String MODIFY_ITEM = MessageUtils.color("&6&lModify -> &e&l%s");
	private static final String CONFIRM_ITEM = MessageUtils.color("&6&lResult = &e&l%s");
	private static final String ZERO_ITEM = MessageUtils.color("&6Set to &e&l0");

	private int result;

	public IntegerFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field) {
		super(plugin, parent, path, field);
		try {
			field.setAccessible(true);
			result = field.getInt(path.getLast());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initializeInventory() {

		this.inventory = Bukkit.createInventory(this, 27, String.format(MENU_TITLE, field.getName()));

		registerClickConsumer(9, getModifyItem(-100, wrapper.skullFromValue(ARROW3_LEFT_HEAD)),
				getModifyConsumer(-100));
		registerClickConsumer(10, getModifyItem(-10, wrapper.skullFromValue(ARROW2_LEFT_HEAD)), getModifyConsumer(-10));
		registerClickConsumer(11, getModifyItem(-1, wrapper.skullFromValue(ARROW_LEFT_HEAD)), getModifyConsumer(-1));
		registerClickConsumer(13, getConfirmItem(), getConfirmConsumer());
		registerClickConsumer(15, getModifyItem(+1, wrapper.skullFromValue(ARROW_RIGHT_HEAD)), getModifyConsumer(+1));
		registerClickConsumer(16, getModifyItem(+10, wrapper.skullFromValue(ARROW2_RIGHT_HEAD)),
				getModifyConsumer(+10));
		registerClickConsumer(17, getModifyItem(+100, wrapper.skullFromValue(ARROW3_RIGHT_HEAD)),
				getModifyConsumer(+100));
		registerClickConsumer(22, getZeroItem(), getModifyConsumer(-result));
		fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);

	}

	private Consumer<InventoryClickEvent> getModifyConsumer(int i) {
		return e -> {
			if (e.getClick().equals(ClickType.LEFT)) {
				result += i;
				inventory.setItem(13, getConfirmItem());
			}
		};
	}

	private Consumer<InventoryClickEvent> getConfirmConsumer() {
		return e -> {
			HumanEntity p = e.getWhoClicked();
			try {
				field.set(path.getLast(), result);
				field.setAccessible(false);
				path.updateModel();
				p.sendMessage(String.format(MODIFY_SUCCESS, field.getName(), String.valueOf(result)));
				if (parent != null) {
					p.openInventory(parent.getInventory());
				} else {
					p.closeInventory();
				}
			} catch (Exception ex) {
				p.closeInventory();
			}
		};
	}

	private ItemStack getModifyItem(int i, ItemStack item) {
		return ItemUtils.getNamedItem(item, String.format(MODIFY_ITEM, String.valueOf(i)), new ArrayList<String>());
	}

	private ItemStack getConfirmItem() {
		return ItemUtils.getNamedItem(wrapper.skullFromValue(SUBMIT_HEAD),
				String.format(CONFIRM_ITEM, String.valueOf(result)), new ArrayList<String>());
	}

	private ItemStack getZeroItem() {
		return ItemUtils.getNamedItem(wrapper.skullFromValue(ZERO_HEAD), ZERO_ITEM, new ArrayList<String>());
	}

}
