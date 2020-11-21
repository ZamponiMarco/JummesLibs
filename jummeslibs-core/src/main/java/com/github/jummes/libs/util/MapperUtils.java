package com.github.jummes.libs.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapperUtils {

    private static final Map<EquipmentSlot, Material> SLOT_ITEMS = new ImmutableMap.Builder<EquipmentSlot, Material>().
            put(EquipmentSlot.HAND, Material.IRON_SWORD).put(EquipmentSlot.OFF_HAND, Material.SHIELD).
            put(EquipmentSlot.FEET, Material.IRON_BOOTS).put(EquipmentSlot.LEGS, Material.IRON_LEGGINGS).
            put(EquipmentSlot.CHEST, Material.IRON_CHESTPLATE).put(EquipmentSlot.HEAD, Material.IRON_HELMET).build();

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

    public static Function<Object, ItemStack> getEquipmentSlotMapper() {
        return obj -> {
            EquipmentSlot slot = (EquipmentSlot) obj;
            return ItemUtils.getNamedItem(new ItemStack(SLOT_ITEMS.get(slot)), slot.name(), Lists.newArrayList());
        };
    }
}
