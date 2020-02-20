package com.github.jummes.libs.gui.model.create;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.annotation.Enumerable;
import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.FieldInventoryHolderFactory;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

/**
 * Class that handles the creation of new Model objects throught the use of a
 * GUI
 * 
 * @author Marco
 *
 */
public class ModelCreateInventoryHolder extends ModelObjectInventoryHolder {

	private static final String CONFIRM_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdiNjJkMjc1ZDg3YzA5Y2UxMGFjYmNjZjM0YzRiYTBiNWYxMzVkNjQzZGM1MzdkYTFmMWRmMzU1YTIyNWU4MiJ9fX0";
	private final static String ITEM_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzAzMDgyZjAzM2Y5NzI0Y2IyMmZlMjdkMGRlNDk3NTA5MDMzNTY0MWVlZTVkOGQ5MjdhZGY1YThiNjdmIn19fQ==";

	protected Field field;

	public ModelCreateInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path,
			Field field) {
		super(plugin, parent, path);
		this.field = field;
	}

	@Override
	protected void initializeInventory() {
		try {
			boolean isCollection = ClassUtils.isAssignable(field.getType(), Collection.class);
			Class<Model> model = getModelClassFromField(field, isCollection);
			this.inventory = Bukkit.createInventory(this, 27,
					MessageUtils.color("&6Create a &c&l" + model.getSimpleName()));
			if (model.isAnnotationPresent(Enumerable.class)) {
				List<Class<? extends Model>> classes = Lists
						.newArrayList(model.getAnnotation(Enumerable.class).classArray());
				classes.forEach(clazz -> {
					registerClickConsumer(classes.indexOf(clazz),
							ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(ITEM_HEAD),
									MessageUtils.color("&6new &c&l" + clazz.getSimpleName()), new ArrayList<String>()),
							getModelCreateConsumer(clazz, isCollection));
				});
			} else {
				registerClickConsumer(13,
						ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(CONFIRM_HEAD),
								MessageUtils.color("&6&lConfirm"), new ArrayList<String>()),
						getModelCreateConsumer(model, isCollection));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		registerClickConsumer(26, getBackItem(), getBackConsumer());
		fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
	}

	private Class<Model> getModelClassFromField(Field field, boolean isCollection) {
		Class<?> fieldClass = field.getType();
		return isCollection
				? (Class<Model>) TypeToken.of(field.getGenericType()).resolveType(fieldClass.getTypeParameters()[0])
						.getRawType()
				: (Class<Model>) fieldClass;
	}

	private Consumer<InventoryClickEvent> getModelCreateConsumer(Class<? extends Model> model, boolean isCollection) {
		return e -> {
			try {
				Model newModel = model.newInstance();
				if (isCollection) {
					((Collection<Model>) FieldUtils.readField(field,
							path.getLast() == null ? path.getModelManager() : path.getLast(), true)).add(newModel);
					path.addModel(newModel);
					e.getWhoClicked()
							.openInventory(new ModelObjectInventoryHolder(plugin, parent, path).getInventory());
				} else {
					FieldUtils.writeField(field, path.getLast(), newModel, true);
					e.getWhoClicked().openInventory(FieldInventoryHolderFactory
							.createFieldInventoryHolder(plugin, parent, path, field, e).getInventory());
				}
				path.saveModel();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		};
	}

}
