package com.github.jummes.libs.model.math;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.model.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@SerializableAs("IntRange")
public class IntRange implements Model {

	private static final String MIN_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQ3MmM5ZDYyOGJiMzIyMWVmMzZiNGNiZDBiOWYxNWVkZDU4ZTU4NjgxODUxNGQ3ZTgyM2Q1NWM0OGMifX19=";
	private static final String MAX_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTUxNDlkZGRhZGVkMjBkMjQ0ZTBiYjYyYTJkOWZhMGRjNmM2YTc4NjI1NTkzMjhhOTRmNzc3MjVmNTNjMzU4In19fQ=";
	
	@Serializable(headTexture = MIN_HEAD)
	private int min;
	@Serializable(headTexture = MAX_HEAD)
	private int max;
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("==", "IntRange");
		map.put("min", min);
		map.put("max", max);
		return map;
	}
	
	public static IntRange deserialize(Map<String, Object> map) {
		int min = (int) map.get("min");
		int max = (int) map.get("max");
		return new IntRange(min, max);
	}
	
	@Override
	public ItemStack getGUIItem() {
		return null;
	}
	
}
