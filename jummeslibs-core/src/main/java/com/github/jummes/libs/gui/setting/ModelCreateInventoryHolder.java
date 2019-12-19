package com.github.jummes.libs.gui.setting;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;

/**
 * Class that handles the creation of new Model objects throught the use of a
 * GUI
 * 
 * @author Marco
 *
 */
public abstract class ModelCreateInventoryHolder extends ModelObjectInventoryHolder<Model> {

	protected boolean requiresId;

	public ModelCreateInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path,
			boolean requiresId) {
		super(plugin, parent, path);
		this.requiresId = requiresId;
	}

}
