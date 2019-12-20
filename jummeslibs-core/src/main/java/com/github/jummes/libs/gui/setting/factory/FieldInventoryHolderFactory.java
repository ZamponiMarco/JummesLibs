package com.github.jummes.libs.gui.setting.factory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelCollectionInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.setting.IntegerFieldChangeInventoryHolder;
import com.github.jummes.libs.gui.setting.StringFieldChangeInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;

public class FieldInventoryHolderFactory {

	@SuppressWarnings("unchecked")
	public static PluginInventoryHolder createFieldInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field) throws Exception {
		String className = field.getType().getName();
		switch (className) {
		case "int":
			return new IntegerFieldChangeInventoryHolder(plugin, parent, path, field);
		case "java.lang.String":
			return new StringFieldChangeInventoryHolder(plugin, parent, path, field);
		}
		if (Collection.class.isAssignableFrom(field.getType()) && Model.class.isAssignableFrom(Class
				.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName()))) {
			field.setAccessible(true);
			Collection<Model> models = (Collection<Model>) field.get(path.getLast());
			field.setAccessible(false);
			return new ModelCollectionInventoryHolder<>(plugin, parent, path, models, 1);
		} else if (Model.class.isAssignableFrom(field.getType())) {
			field.setAccessible(true);
			path.addModel((Model) field.get(path.getLast()));
			field.setAccessible(false);
			return new ModelObjectInventoryHolder<>(plugin, parent, path);
		}
		return null;
	}
}
