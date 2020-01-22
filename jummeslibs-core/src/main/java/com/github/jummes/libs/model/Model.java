package com.github.jummes.libs.model;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a class that contains data, usually configured as a Java Bean
 * object, from this class data managers and databases can be built it has to be
 * configuration serializable
 * 
 * @author Marco
 *
 */
public interface Model extends ConfigurationSerializable {
	
	public abstract ItemStack getGUIItem();
	
}