package com.github.jummes.libs.gui.setting;

import java.lang.reflect.Field;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;

/**
 * Class that handles the change of fields of a Model throught the use of a GUI
 * 
 * @author Marco
 *
 */
public abstract class FieldChangeInventoryHolder extends ModelObjectInventoryHolder {

	protected Field field;

	public FieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field) {
		super(plugin, parent, path);
		this.field = field;
	}

}
