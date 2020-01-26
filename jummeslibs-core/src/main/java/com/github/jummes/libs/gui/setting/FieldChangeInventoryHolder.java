package com.github.jummes.libs.gui.setting;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.setting.change.ChangeInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;

/**
 * Class that handles the change of fields of a Model throught the use of a GUI
 * 
 * @author Marco
 *
 */
public abstract class FieldChangeInventoryHolder extends ModelObjectInventoryHolder {

	protected ChangeInformation changeInformation;

	public FieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path,
			ChangeInformation changeInformation) {
		super(plugin, parent, path);
		this.changeInformation = changeInformation;
	}

	public static Map<Class<?>, Class<? extends FieldChangeInventoryHolder>> getInventories() {
		Map<Class<?>, Class<? extends FieldChangeInventoryHolder>> map = new HashMap<Class<?>, Class<? extends FieldChangeInventoryHolder>>();
		map.put(int.class, IntegerFieldChangeInventoryHolder.class);
		map.put(double.class, DoubleFieldChangeInventoryHolder.class);
		map.put(String.class, StringFieldChangeInventoryHolder.class);
		return map;
	}

}
