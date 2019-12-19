package com.github.jummes.libs.gui.model;

import java.lang.reflect.Field;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;

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

	public ModelObjectInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path) {
		super(plugin, parent);
		this.path = path;
	}

	@Override
	protected void initializeInventory() {
		this.inventory = Bukkit.createInventory(this, 27, path.getLast().getClass().getSimpleName());
		
		Field[] fields = path.getLast().getClass().getDeclaredFields();
		IntStream.range(0, fields.length).forEach(i -> {
			registerClickConsumer(i, new ItemStack(Material.CARROT), e -> {
				System.out.println(fields[i].getName());
			});
		});
		
		fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
	}

}
