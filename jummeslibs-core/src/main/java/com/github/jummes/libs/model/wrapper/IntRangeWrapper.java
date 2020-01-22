package com.github.jummes.libs.model.wrapper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import com.github.jummes.libs.annotation.GUISerializable;
import com.github.jummes.libs.model.Model;

@SerializableAs("IntRange")
public class IntRangeWrapper extends ModelWrapper<IntRange> implements Model{

	private static final String X_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkxZDZlZGE4M2VkMmMyNGRjZGNjYjFlMzNkZjM2OTRlZWUzOTdhNTcwMTIyNTViZmM1NmEzYzI0NGJjYzQ3NCJ9fX0=";
	
	@GUISerializable(headTexture = X_HEAD)
	private int min;
	@GUISerializable(headTexture = X_HEAD)
	private int max;
	
	public IntRangeWrapper(IntRange wrapped) {
		super(wrapped);
		this.min = wrapped.getMinimumInteger();
		this.max = wrapped.getMaximumInteger();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("==", "IntRange");
		map.put("min", min);
		map.put("max", max);
		return map;
	}
	
	public static IntRangeWrapper deserialize(Map<String, Object> map) {
		int min = (int) map.get("min");
		int max = (int) map.get("max");
		return new IntRangeWrapper(new IntRange(min, max));
	}

	@Override
	public ItemStack getGUIItem() {
		return null;
	}

	
	
}
