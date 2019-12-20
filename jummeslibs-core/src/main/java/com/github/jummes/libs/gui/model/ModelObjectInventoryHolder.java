package com.github.jummes.libs.gui.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.annotation.GUISerializable;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.setting.factory.FieldInventoryHolderFactory;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;

/**
 * An InventoryHolder that contains in memory them modelPath used to arrive to
 * him, it usually allows the modification of parameters of models and its
 * subclasses
 * 
 * @author Marco
 *
 */
public class ModelObjectInventoryHolder<T extends Model> extends PluginInventoryHolder {

	protected ModelPath<? extends Model> path;

	public ModelObjectInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path) {
		super(plugin, parent);
		this.path = path;
	}

	@Override
	protected void initializeInventory() {
		this.inventory = Bukkit.createInventory(this, 27, path.getLast().getClass().getSimpleName());

		Field[] fields = path.getLast().getClass().getDeclaredFields();
		Field[] toPrint = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(GUISerializable.class))
				.toArray(size -> new Field[size]);

		IntStream.range(0, toPrint.length).forEach(i -> {
			registerClickConsumer(i,
					ItemUtils.getNamedItem(
							wrapper.skullFromValue(toPrint[i].getAnnotation(GUISerializable.class).headTexture()),
							toPrint[i].getName(), new ArrayList<String>()),
					e -> {
						try {
							e.getWhoClicked().openInventory(FieldInventoryHolderFactory
									.createFieldInventoryHolder(plugin, this, path, toPrint[i]).getInventory());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					});
		});

		fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
	}

}
