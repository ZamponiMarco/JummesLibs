package com.github.jummes.libs.gui.model;

import java.lang.reflect.Field;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;

/**
 * Class that handles the creation of new Model objects throught the use of a
 * GUI
 * 
 * @author Marco
 *
 */
public class ModelCreateInventoryHolder extends ModelObjectInventoryHolder<Model> {

	protected Field field;
	protected boolean requiresId;

	public ModelCreateInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path,
			Field field, boolean requiresId) {
		super(plugin, parent, path);
		this.field = field;
		this.requiresId = requiresId;
	}
	
	@Override
		protected void initializeInventory() {
			super.initializeInventory();
		}

}
