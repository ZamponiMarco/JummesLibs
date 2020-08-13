package com.github.jummes.libs.wrapper;


import net.minecraft.server.v1_16_R2.NBTTagCompound;
import net.minecraft.server.v1_16_R2.NBTTagList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class VersionWrapper_v1_16_R2 implements VersionWrapper {

    @Override
    public ItemStack skullFromValue(String value) {
        UUID id = new UUID(value.hashCode(), value.hashCode());
        net.minecraft.server.v1_16_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(new ItemStack(Material.PLAYER_HEAD));
        NBTTagCompound tag = nmsItem.getOrCreateTag();
        NBTTagCompound skullOwner = new NBTTagCompound();
        NBTTagCompound texturesCompound = new NBTTagCompound();
        NBTTagList textures = new NBTTagList();
        NBTTagCompound textureValue = new NBTTagCompound();
        textureValue.setString("Value", value);
        textures.add(textureValue);
        texturesCompound.set("textures", textures);
        skullOwner.set("Properties", texturesCompound);
        skullOwner.setIntArray("Id", getUUIDIntArray(id));
        tag.set("SkullOwner", skullOwner);
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

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends ItemMeta> getCraftMetaItemClass() {
        try {
            return (Class<? extends ItemMeta>) Class.forName("org.bukkit.craftbukkit.v1_16_R2.inventory.CraftMetaItem");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ItemStack addTagToItem(ItemStack item, String key, String value) {
        net.minecraft.server.v1_16_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getOrCreateTag();
        tag.setString(key, value);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public String getTagItem(ItemStack item, String key) {
        net.minecraft.server.v1_16_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getOrCreateTag();
        return tag.getString(key);
    }

}
