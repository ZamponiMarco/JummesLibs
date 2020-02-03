package com.github.jummes.libs.gui;

import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.model.ModelCollectionInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.model.ObjectCollectionInventoryHolder;
import com.github.jummes.libs.gui.model.create.ModelCreateInventoryHolder;
import com.github.jummes.libs.gui.setting.FieldChangeInventoryHolder;
import com.github.jummes.libs.gui.setting.StringFieldChangeInventoryHolder;
import com.github.jummes.libs.gui.setting.change.ChangeInformation;
import com.github.jummes.libs.gui.setting.change.CollectionChangeInformation;
import com.github.jummes.libs.gui.setting.change.EnumChangeInformation;
import com.github.jummes.libs.gui.setting.change.FieldChangeInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.model.wrapper.ModelWrapper;
import com.google.common.reflect.TypeToken;

public class FieldInventoryHolderFactory {

	public static PluginInventoryHolder createFieldInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field, ClickType clickType) {
		try {
			Class<?> clazz = field.getType();
			if (FieldChangeInventoryHolder.getInventories().keySet().contains(clazz)) {
				return FieldChangeInventoryHolder.getInventories().get(clazz)
						.getConstructor(JavaPlugin.class, PluginInventoryHolder.class, ModelPath.class,
								ChangeInformation.class)
						.newInstance(plugin, parent, path, new FieldChangeInformation(field));
			} else if (ModelWrapper.getWrappers().keySet().contains(clazz)) {
				path.addModel((Model) ModelWrapper.getWrappers().get(clazz).getConstructor(clazz)
						.newInstance(FieldUtils.readField(field, path.getLast(), true)));
				return new ModelObjectInventoryHolder(plugin, parent, path);
			} else if (ClassUtils.isAssignable(clazz, Collection.class)) {
				Class<?> containedClass = TypeToken.of(field.getGenericType()).resolveType(clazz.getTypeParameters()[0])
						.getRawType();
				if (ClassUtils.isAssignable(containedClass, Model.class)) {
					return new ModelCollectionInventoryHolder(plugin, parent, path, field, 1);
				}
				return new ObjectCollectionInventoryHolder(plugin, parent, path, field, 1);
			} else if (Model.class.isAssignableFrom(clazz)) {
				if (clickType.equals(ClickType.LEFT)) {
					path.addModel((Model) FieldUtils.readField(field, path.getLast(), true));
					return new ModelObjectInventoryHolder(plugin, parent, path);
				} else if (clickType.equals(ClickType.RIGHT)) {
					return new ModelCreateInventoryHolder(plugin, parent, path, field);
				}
			} else if (clazz.isEnum()) {
				return new StringFieldChangeInventoryHolder(plugin, parent, path, new EnumChangeInformation(field));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parent;
	}

	public static PluginInventoryHolder createFieldInCollectionInventoryHolder(JavaPlugin plugin,
			PluginInventoryHolder parent, ModelPath<? extends Model> path, Field field, Object currentValue) {
		Class<?> clazz = field.getType();
		Class<?> containedClass = TypeToken.of(field.getGenericType()).resolveType(clazz.getTypeParameters()[0])
				.getRawType();
		try {
			if (FieldChangeInventoryHolder.getInventories().keySet().contains(containedClass)) {
				return FieldChangeInventoryHolder.getInventories().get(containedClass)
						.getConstructor(JavaPlugin.class, PluginInventoryHolder.class, ModelPath.class,
								ChangeInformation.class)
						.newInstance(plugin, parent, path, new CollectionChangeInformation(field, currentValue));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parent;
	}

}
