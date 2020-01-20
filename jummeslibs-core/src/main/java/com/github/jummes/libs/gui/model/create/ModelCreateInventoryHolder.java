package com.github.jummes.libs.gui.model.create;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.setting.factory.FieldInventoryHolderFactory;
import com.github.jummes.libs.model.EnumerationModel;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;

/**
 * Class that handles the creation of new Model objects throught the use of a
 * GUI
 * 
 * @author Marco
 *
 */
public class ModelCreateInventoryHolder extends ModelObjectInventoryHolder {

	protected Field field;

	public ModelCreateInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path,
			Field field) {
		super(plugin, parent, path);
		this.field = field;
	}

	@Override
	protected void initializeInventory() {
		this.inventory = Bukkit.createInventory(this, 27, "sas");

		try {
			field.setAccessible(true);
			Class<?> modelClass = field.getType();
			if (EnumerationModel.class.isAssignableFrom(modelClass) || Collection.class.isAssignableFrom(modelClass)
					&& EnumerationModel.class.isAssignableFrom(Class.forName(
							((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName()))) {
				Class<EnumerationModel> model = Collection.class.isAssignableFrom(modelClass)
						? (Class<EnumerationModel>) Class.forName(
								((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName())
						: (Class<EnumerationModel>) field.getType();
				List<Class<?>> classes = (List<Class<?>>) model.getMethod("getClassList").invoke(null);
				classes.forEach(clazz2 -> {
					registerClickConsumer(classes.indexOf(clazz2), getBackItem(), e -> {
						field.setAccessible(true);
						try {
							if (Collection.class.isAssignableFrom(modelClass)) {
								if (path.getLast() == null) {
									Model newModel = (Model) clazz2.newInstance();
									((Collection<Model>) field.get(path.getModelManager())).add(newModel);
									path.addModel(newModel);
									path.updateModel();
									path.removeModel();
								} else {
									((Collection<Model>) field.get(path.getLast())).add((Model) clazz2.newInstance());
									path.updateModel();
								}
							} else {
								field.set(path.getLast(), clazz2.newInstance());
							}
							e.getWhoClicked()
									.openInventory(FieldInventoryHolderFactory
											.createFieldInventoryHolder(plugin, parent, path, field, e.getClick())
											.getInventory());
							field.setAccessible(false);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					});
				});
			} else {
				registerClickConsumer(13, getBackItem(), e -> {
					try {
						field.setAccessible(true);
						if (Collection.class.isAssignableFrom(modelClass)) {
							Class<Model> collectionClass = (Class<Model>) Class
									.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]
											.getTypeName());
							if (path.getLast() == null) {
								Model newModel = collectionClass.newInstance();
								((Collection<Model>) field.get(path.getModelManager())).add(newModel);
								path.addModel(newModel);
								path.updateModel();
								path.removeModel();
							} else {
								((Collection<Model>) field.get(path.getLast())).add(collectionClass.newInstance());
								path.updateModel();
							}
						} else {
							field.set(path.getLast(), modelClass.newInstance());
							path.updateModel();
						}
						e.getWhoClicked().openInventory(FieldInventoryHolderFactory
								.createFieldInventoryHolder(plugin, parent, path, field, e.getClick()).getInventory());
						field.setAccessible(false);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
			}
			field.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
	}

}
