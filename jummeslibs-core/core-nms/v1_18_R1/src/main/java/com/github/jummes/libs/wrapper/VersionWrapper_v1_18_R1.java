package com.github.jummes.libs.wrapper;


import com.github.jummes.libs.wrapper.VersionWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.UUID;

public class VersionWrapper_v1_18_R1 implements VersionWrapper {

    @Override
    public org.bukkit.inventory.ItemStack skullFromValue(String value) {
        UUID id = new UUID(value.hashCode(), value.hashCode());
        ItemStack nmsItem = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD));
        CompoundTag tag = nmsItem.getOrCreateTag();
        CompoundTag skullOwner = new CompoundTag();
        CompoundTag texturesCompound = new CompoundTag();
        ListTag textures = new ListTag();
        CompoundTag textureValue = new CompoundTag();
        textureValue.putString("Value", value);
        textures.add(textureValue);
        texturesCompound.put("textures", textures);
        skullOwner.put("Properties", texturesCompound);
        skullOwner.putIntArray("Id", getUUIDIntArray(id));
        tag.put("SkullOwner", skullOwner);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    private int[] getUUIDIntArray(UUID id) {
        int[] ints = new int[4];
        int leastSignificantBitsMask = 0xFF;
        ints[0] = (int) (id.getMostSignificantBits() >> 8);
        ints[1] = (int) (id.getMostSignificantBits() & leastSignificantBitsMask);
        ints[2] = (int) (id.getLeastSignificantBits() >> 8);
        ints[3] = (int) (id.getLeastSignificantBits() & leastSignificantBitsMask);
        return ints;
    }

    public ItemMeta deserializeItemMeta(Map<String, Object> map) {
        Class clazz;
        try {
            clazz = Class.forName("org.bukkit.craftbukkit.v1_18_R1.inventory.CraftMetaItem");
            Class innerClass = clazz.getDeclaredClasses()[1];
            return (ItemMeta) innerClass.getMethod("deserialize", Map.class).invoke(null, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
