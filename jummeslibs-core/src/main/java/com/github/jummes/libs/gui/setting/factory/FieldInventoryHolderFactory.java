package com.github.jummes.libs.gui.setting.factory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelCollectionInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.model.create.ModelCreateInventoryHolder;
import com.github.jummes.libs.gui.setting.DoubleFieldChangeInventoryHolder;
import com.github.jummes.libs.gui.setting.IntegerFieldChangeInventoryHolder;
import com.github.jummes.libs.gui.setting.StringFieldChangeInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.model.wrapper.ModelWrapper;

public class FieldInventoryHolderFactory {

	public static PluginInventoryHolder createFieldInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field, ClickType clickType) {
		try {
			Class<?> clazz = field.getType();
			if (clazz.equals(int.class)) {
				return new IntegerFieldChangeInventoryHolder(plugin, parent, path, field);
			} else if (clazz.equals(double.class)) {
				return new DoubleFieldChangeInventoryHolder(plugin, parent, path, field);
			} else if (clazz.equals(String.class)) {
				return new StringFieldChangeInventoryHolder(plugin, parent, path, field);
			} else if (ModelWrapper.getWrappers().keySet().contains(clazz)) {
				path.addModel((Model) ModelWrapper.getWrappers().get(clazz).getConstructor(clazz)
						.newInstance(FieldUtils.readField(field, path.getLast(), true)));
				return new ModelObjectInventoryHolder(plugin, parent, path);
			} else if (Collection.class.isAssignableFrom(clazz) && Model.class.isAssignableFrom(Class
					.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName()))) {
				return new ModelCollectionInventoryHolder(plugin, parent, path, field, 1);
			} else if (Model.class.isAssignableFrom(clazz)) {
				if (clickType.equals(ClickType.LEFT)) {
					path.addModel((Model) FieldUtils.readField(field, path.getLast(), true));
					return new ModelObjectInventoryHolder(plugin, parent, path);
				} else if (clickType.equals(ClickType.RIGHT)) {
					return new ModelCreateInventoryHolder(plugin, parent, path, field);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
