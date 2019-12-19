package com.github.jummes.libs.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

	/**
	 * Gets a named item
	 * 
	 * @param item item that works as the base
	 * @param name name that will be displayed
	 * @return
	 */
	public static ItemStack getNamedItem(ItemStack item, String name, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageUtils.color(name));
		meta.setLore(null);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Gets an item with no name
	 * 
	 * @param material the material of the item
	 * @return an item with a blank name
	 */
	public static ItemStack getNotNamedItem(Material material) {
		return getNamedItem(new ItemStack(material), " ", new ArrayList<String>());
	}

}