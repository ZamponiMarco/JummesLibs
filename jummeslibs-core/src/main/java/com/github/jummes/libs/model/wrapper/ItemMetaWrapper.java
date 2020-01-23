package com.github.jummes.libs.model.wrapper;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.jummes.libs.annotation.GUINameable;
import com.github.jummes.libs.annotation.GUISerializable;
import com.github.jummes.libs.annotation.SetterMappable;
import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.model.Model;

import lombok.ToString;

@ToString
@GUINameable(GUIName = "ItemMeta")
@SerializableAs("ItemMeta")
public class ItemMetaWrapper extends ModelWrapper<ItemMeta> implements Model {

	private final static String MATERIAL_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM5ZDZmMTE1Y2MwNzg0MTgzZGZjNzU4NDdmZGI0ZWRkYzI2ZGMxNzgyM2U2ZTA0YTZmNjU1MzUyYTExNzliIn19fQ";

	@GUISerializable(headTexture = MATERIAL_HEAD)
	@SetterMappable(setterMethod = "setDisplayName")
	private String displayName;
	@SetterMappable(setterMethod = "setLore")
	private List<String> lore;

	public ItemMetaWrapper(ItemMeta wrapped) {
		super(wrapped);
		this.displayName = wrapped.getDisplayName();
		this.lore = wrapped.getLore();
	}

	@Override
	public Map<String, Object> serialize() {
		return wrapped.serialize();
	}

	public static ItemMetaWrapper deserialize(Map<String, Object> map) {
		try {
			Constructor<?> cons = Libs.getWrapper().getCraftMetaItemClass().getDeclaredConstructor(Map.class);
			cons.setAccessible(true);
			return new ItemMetaWrapper((ItemMeta) cons.newInstance(map));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ItemStack getGUIItem() {
		return null;
	}

}
