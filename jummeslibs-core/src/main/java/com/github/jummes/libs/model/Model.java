package com.github.jummes.libs.model;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import com.github.jummes.libs.annotation.Serializable;
import com.google.common.collect.Lists;

/**
 * Represents a class that contains data, usually configured as a Java Bean
 * object, from this class data managers and databases can be built it has to be
 * configuration serializable
 * 
 * @author Marco
 *
 */
@SuppressWarnings("unused")
public interface Model extends ConfigurationSerializable {

	public default ItemStack getGUIItem() {
		return null;
	}

	public default void beforeComponentCreation(Class<? extends Model> modelClass) {
	}

	public default void afterComponentCreation(Model model) {
	}

	public default void beforeComponentSetting(Model model) {
	}

	public default void afterComponentSetting(Model model) {
	}

	public default void onModify() {
	}

	public default void onCreation() {
	}

	public default void onRemoval() {
	}

	public default Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		List<Field> fields = Lists.newArrayList(getClass().getDeclaredFields());
		ClassUtils.getAllSuperclasses(getClass()).forEach(
				superClass -> fields.addAll(0, Lists.newArrayList(((Class<?>) superClass).getDeclaredFields())));
		fields.stream().filter(field -> field.isAnnotationPresent(Serializable.class)).forEach(field -> {
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