package com.github.jummes.libs.util;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemUtils {

    /**
     * Gets a named item
     *
     * @param item item that works as the base
     * @param name name that will be displayed
     * @return
     */
    public static ItemStack getNamedItem(ItemStack item, String name, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.color(name));
        meta.setLore(null);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Gets an item with no name
     *
     * @param material the material of the item
     * @return an item with a blank name
     */
    public static ItemStack getNotNamedItem(Material material) {
        return getNamedItem(new ItemStack(material), " ", new ArrayList<String>());
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

    public static List<Object> getMaterialList() {
        return Arrays.stream(Material.values()).filter(
                m -> !m.name().contains("LEGACY") && m.isItem())
                .collect(Collectors.toList());
    }

    public static Function<Object, ItemStack> getMaterialMapper() {
        return obj -> {
            Material material = (Material) obj;
            if (material.equals(Material.AIR)) {
                return ItemUtils.getNamedItem(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE),
                        "&6&lAir", Lists.newArrayList());
            }
            return new ItemStack(material);
        };
    }

}