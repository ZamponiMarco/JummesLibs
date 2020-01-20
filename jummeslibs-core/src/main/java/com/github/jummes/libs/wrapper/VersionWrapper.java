package com.github.jummes.libs.wrapper;

import org.bukkit.inventory.ItemStack;

public interface VersionWrapper {

	/**
	 * Returns the itemstack of a skull from the given texture value
	 * 
	 * @param value texture value in base64
	 * @return ItemStack of the head
	 */
	public ItemStack skullFromValue(String value);

}
