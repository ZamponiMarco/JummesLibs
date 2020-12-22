package com.github.jummes.libs.wrapper;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public interface VersionWrapper {

    /**
     * Returns the itemstack of a skull from the given texture value
     *
     * @param value texture value in base64
     * @return ItemStack of the head
     */
    public ItemStack skullFromValue(String value);

    public ItemStack addTagToItem(ItemStack item, String key, String value);

    public String getTagItem(ItemStack item, String key);

    public ItemMeta deserializeItemMeta(Map<String, Object> map);

}
