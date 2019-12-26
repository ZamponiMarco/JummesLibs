package com.github.jummes.libs.gui.setting;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;

public class StringFieldChangeInventoryHolder extends FieldChangeInventoryHolder {

	private static final String MODIFY_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWE3NWM4ZTUxYzNkMTA1YmFiNGM3ZGUzM2E3NzA5MzczNjRiNWEwMWMxNWI3ZGI4MmNjM2UxZmU2ZWI5MzM5NiJ9fX0==";

	private static final String MODIFY_TITLE = MessageUtils.color("&6&lModify &e&l%s");
	private static final String MODIFY_MESSAGE = MessageUtils.color(
			"&aTo modify the parameter type in chat the &6&lnew value&a.\n&aType &6&l'exit' &ato leave the value unmodified.");
	private static final String MODIFY_ITEM_NAME = MessageUtils.color("&6&lModify Value");

	private static Map<HumanEntity, Entry<ModelPath<? extends Model>, Entry<Field, InventoryHolder>>> settingsMap = new HashMap<>();

	public StringFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field) {
		super(plugin, parent, path, field);
	}

	@Override
	protected void initializeInventory() {
		this.inventory = Bukkit.createInventory(this, 27, String.format(MODIFY_TITLE, field.getName()));

		registerClickConsumer(13, getStringItem(wrapper.skullFromValue(MODIFY_HEAD)),
				e -> playerCanWrite(e.getWhoClicked()));
		registerClickConsumer(26, getBackItem(), getBackConsumer());
		fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
	}

	private void playerCanWrite(HumanEntity entity) {
		entity.sendMessage(MODIFY_MESSAGE);
		settingsMap.put(entity, new AbstractMap.SimpleEntry<>(path, new AbstractMap.SimpleEntry<>(field, parent)));
		entity.closeInventory();
	}

	private ItemStack getStringItem(ItemStack item) {
		return ItemUtils.getNamedItem(item, MODIFY_ITEM_NAME, new ArrayList<String>());
	}

	public static Map<HumanEntity, Entry<ModelPath<? extends Model>, Entry<Field, InventoryHolder>>> getSettingsMap() {
		return settingsMap;
	}

}
