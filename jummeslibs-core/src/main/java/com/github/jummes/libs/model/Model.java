package com.github.jummes.libs.model;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import com.github.jummes.libs.annotation.Serializable;

/**
 * Represents a class that contains data, usually configured as a Java Bean
 * object, from this class data managers and databases can be built it has to be
 * configuration serializable
 * 
 * @author Marco
 *
 */
public interface Model extends ConfigurationSerializable {

	public default ItemStack getGUIItem() {
		return null;
	}

	public default Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Arrays.stream(getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(Serializable.class))
				.forEach(field -> {
					try {
						Object value = FieldUtils.readField(field, this, true);
						map.put(field.getName(),
								field.getAnnotation(Serializable.class).stringValue() ? value.toString() : value);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
		return map;
	}

}