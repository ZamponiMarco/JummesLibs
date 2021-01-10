package com.github.jummes.libs.wrapper;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagList;

public class VersionWrapper_v1_15_R1 implements VersionWrapper {

    @Override
    public ItemStack skullFromValue(String value) {
        UUID id = new UUID(value.hashCode(), value.hashCode());
        net.minecraft.server.v1_15_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(new ItemStack(Material.PLAYER_HEAD));
        NBTTagCompound tag = nmsItem.getOrCreateTag();
        NBTTagCompound skullOwner = new NBTTagCompound();
        NBTTagCompound texturesCompound = new NBTTagCompound();
        NBTTagList textures = new NBTTagList();
        NBTTagCompound textureValue = new NBTTagCompound();
        textureValue.setString("Value", value);
        textures.add(textureValue);
        texturesCompound.set("textures", textures);
        skullOwner.set("Properties", texturesCompound);
        skullOwner.setString("Id", id.toString());
        tag.set("SkullOwner", skullOwner);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public ItemMeta deserializeItemMeta(Map<String, Object> map) {
        Class clazz = null;
        try {
            clazz = Class.forName("org.bukkit.craftbukkit.v1_15_R1.inventory.CraftMetaItem");
            Class innerClass = clazz.getDeclaredClasses()[1];
            return (ItemMeta) innerClass.getMethod("deserialize", Map.class).invoke(null, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ItemStack addTagToItem(ItemStack item, String key, String value) {
        net.minecraft.server.v1_15_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getOrCreateTag();
        tag.setString(key, value);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public String getTagItem(ItemStack item, String key) {
        net.minecraft.server.v1_15_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getOrCreateTag();
        return tag.getString(key);
    }

}
