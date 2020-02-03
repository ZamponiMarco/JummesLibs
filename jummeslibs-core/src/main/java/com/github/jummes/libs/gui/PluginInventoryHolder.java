package com.github.jummes.libs.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;
import com.github.jummes.libs.wrapper.VersionWrapper;

public abstract class PluginInventoryHolder implements InventoryHolder {

	private static final String BACK_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19";

	protected static final VersionWrapper wrapper = Libs.getWrapper();

	protected JavaPlugin plugin;
	protected PluginInventoryHolder parent;

	protected Inventory inventory;
	protected Map<Integer, Consumer<InventoryClickEvent>> clickMap;

	public PluginInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent) {
		this.plugin = plugin;
		this.parent = parent;
		this.clickMap = new HashMap<>();
	}

	protected abstract void initializeInventory();

	/**
	 * Handles the InventoryClickEvent depending on the slot that has been clicked
	 * and the content of the clickMap
	 * 
	 * @param e the event that has been fired
	 */
	public void handleClickEvent(InventoryClickEvent e) {
		try {
			clickMap.get(e.getSlot()).accept(e);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		e.setCancelled(true);
	}

	/**
	 * Register an event consumer on a determined slot and puts an item in such slot
	 * 
	 * @param slot          slot that the event will be linked to
	 * @param item          item that will be put in the inventory in the slot
	 * @param clickConsumer event consumer that will be called when the event is
	 *                      fired
	 */
	protected void registerClickConsumer(int slot, ItemStack item, Consumer<InventoryClickEvent> clickConsumer) {
		inventory.setItem(slot, item);
		clickMap.put(slot, clickConsumer);
	}

	/**
	 * Fills the inventory with a material with an empty name
	 * 
	 * @param material
	 */
	protected void fillInventoryWith(Material material) {
		IntStream.range(0, inventory.getSize()).forEach(i -> {
			if (inventory.getItem(i) == null) {
				registerClickConsumer(i, ItemUtils.getNotNamedItem(material), e -> {
				});
			}
		});
	}

	/**
	 * Gets the ItemStack that represents the back button
	 * 
	 * @return the back button item
	 */
	protected ItemStack getBackItem() {
		return ItemUtils.getNamedItem(wrapper.skullFromValue(BACK_HEAD), MessageUtils.color("&6&lBack"),
				new ArrayList<String>());
	}

	/**
	 * Gets the action that will be played when the back button will be pressed
	 * 
	 * @return consumer called when the back button is pressed
	 */
	protected Consumer<InventoryClickEvent> getBackConsumer() {
		return e -> {
			if (parent != null) {
				e.getWhoClicked().openInventory(parent.getInventory());
			} else {
				e.getWhoClicked().closeInventory();
			}
		};
	}

	/**
	 * Returns the updated inventory
	 */
	@Override
	public Inventory getInventory() {
		initializeInventory();
		return inventory;
	}

}