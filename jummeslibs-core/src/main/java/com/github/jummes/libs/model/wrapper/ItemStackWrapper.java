package com.github.jummes.libs.model.wrapper;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.annotation.CustomClickable;
import com.github.jummes.libs.annotation.GUINameable;
import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.annotation.SetterMappable;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.model.create.ModelCreateInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;

import lombok.ToString;

@ToString
@CustomClickable(customClickConsumer = "getCustomClickConsumer")
@GUINameable(GUIName = "Item")
@SerializableAs("ItemStackWrapper")
public class ItemStackWrapper extends ModelWrapper<ItemStack> implements Model {

	private static final String MIN_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ3MmM5ZDYyOGJiMzIyMWVmMzZiNGNiZDBiOWYxNWVkZDU4ZTU4NjgxODUxNGQ3ZTgyM2Q1NWM0OGMifX19=";

	@Serializable(headTexture = MIN_HEAD)
	private Material type;
	@Serializable(headTexture = MIN_HEAD)
	@SetterMappable(setterMethod = "setAmount")
	private int amount;
	@Serializable(headTexture = MIN_HEAD)
	private ItemMetaWrapper meta;

	public ItemStackWrapper(ItemStack wrapped) {
		super(wrapped);
		this.type = wrapped.getType();
		this.amount = wrapped.getAmount();
		this.meta = new ItemMetaWrapper(wrapped.getItemMeta());
	}

	public PluginInventoryHolder getCustomClickConsumer(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field, InventoryClickEvent e) {
		try {
			if (e.getClick().equals(ClickType.LEFT)) {
				if (!e.getCursor().getType().equals(Material.AIR)) {
					ItemStack newItem = e.getCursor().clone();
					this.wrapped = newItem;
					this.amount = newItem.getAmount();
					this.type = newItem.getType();
					this.meta = new ItemMetaWrapper(newItem.getItemMeta());
					path.saveModel();
					e.getWhoClicked().getInventory().addItem(newItem);
					e.getCursor().setAmount(0);
				}
				path.addModel(this);
				return new ModelObjectInventoryHolder(plugin, parent, path);
			} else if (e.getClick().equals(ClickType.RIGHT)) {
				return new ModelCreateInventoryHolder(plugin, parent, path, field);
			}
		} catch (Exception e1) {
		}
		return null;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = wrapped.serialize();
		map.put("meta", meta);
		return map;
	}

	public static ItemStackWrapper deserialize(Map<String, Object> map) {
		ItemStack wrapped = ItemStack.deserialize(map);
		ItemMetaWrapper metaWrapper = (ItemMetaWrapper) map.getOrDefault("meta", null);
		wrapped.setItemMeta(metaWrapper != null ? metaWrapper.wrapped : null);
		ItemStackWrapper wrapper = new ItemStackWrapper(wrapped);
		return wrapper;
	}

	@Override
	public ItemStack getGUIItem() {
		return null;
	}

}
