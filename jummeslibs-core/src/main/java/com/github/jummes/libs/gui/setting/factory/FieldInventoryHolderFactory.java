package com.github.jummes.libs.gui.setting.factory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelCollectionInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.setting.IntegerFieldChangeInventoryHolder;
import com.github.jummes.libs.gui.setting.StringFieldChangeInventoryHolder;
import com.github.jummes.libs.model.EnumerationModel;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;

public class FieldInventoryHolderFactory {

	@SuppressWarnings("unchecked")
	public static PluginInventoryHolder createFieldInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field, ClickType clickType) throws Exception {
		Class<?> clazz = field.getType();
		if (clazz.equals(int.class)) {
			return new IntegerFieldChangeInventoryHolder(plugin, parent, path, field);
		} else if (clazz.equals(String.class)) {
			return new StringFieldChangeInventoryHolder(plugin, parent, path, field);
		} else if (Collection.class.isAssignableFrom(clazz) && Model.class.isAssignableFrom(Class
				.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName()))) {
			field.setAccessible(true);
			Collection<Model> models = (Collection<Model>) field.get(path.getLast());
			field.setAccessible(false);
			return new ModelCollectionInventoryHolder<>(plugin, parent, path, models, 1);
		} else if (EnumerationModel.class.isAssignableFrom(clazz) && clickType.equals(ClickType.RIGHT)) {
			
		} else if (Model.class.isAssignableFrom(clazz)) {
			field.setAccessible(true);
			path.addModel((Model) field.get(path.getLast()));
			field.setAccessible(false);
			return new ModelObjectInventoryHolder<>(plugin, parent, path);
		}
		return null;
	}
}
