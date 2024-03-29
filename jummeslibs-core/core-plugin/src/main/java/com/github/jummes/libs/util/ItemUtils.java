package com.github.jummes.libs.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class ItemUtils {

    public static ItemStack getNamedItem(ItemStack item, Component name, List<Component> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getNotNamedItem(Material material) {
        return getNamedItem(new ItemStack(material), MessageUtils.color(" "), new ArrayList<>());
    }

    public static boolean isSimilar(ItemStack first, ItemStack second) {
        if (first == null || second == null) {
            return false;
        }
        boolean sameType = (first.getType() == second.getType());
        boolean sameHasItemMeta = (first.hasItemMeta() == second.hasItemMeta());
        boolean sameEnchantments = (first.getEnchantments().equals(second.getEnchantments()));
        boolean sameItemMeta = true;
        if (first.hasItemMeta() && second.hasItemMeta()) {
            sameItemMeta = first.getItemMeta().toString().equals(second.getItemMeta().toString());
        }
        return sameType && sameHasItemMeta && sameItemMeta && sameEnchantments;
    }
}