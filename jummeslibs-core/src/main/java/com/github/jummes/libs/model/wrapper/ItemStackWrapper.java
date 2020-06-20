package com.github.jummes.libs.model.wrapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.annotation.CustomClickable;
import com.github.jummes.libs.annotation.GUINameable;
import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.annotation.SetterMappable;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.model.create.ModelCreateInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;

import lombok.ToString;

@ToString
@CustomClickable(customFieldClickConsumer = "getCustomClickConsumer")
@GUINameable(GUIName = "Item")
public class ItemStackWrapper extends ModelWrapper<ItemStack> implements Model {

    private static final String MATERIAL_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2E2ZTUzYmZiN2MxM2ZlZGJkZmU4OTY3NmY4MWZjMmNhNzk3NDYzNGE2ODQxNDFhZDFmNTE2NGYwZWRmNGEyIn19fQ=====";
    private static final String AMOUNT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjdkYzNlMjlhMDkyM2U1MmVjZWU2YjRjOWQ1MzNhNzllNzRiYjZiZWQ1NDFiNDk1YTEzYWJkMzU5NjI3NjUzIn19fQ===";
    private static final String META_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4MzM0MTUxYzIzNGY0MTY0NzExM2JlM2VhZGYyODdkMTgxNzExNWJhYzk0NDVmZmJiYmU5Y2IyYjI4NGIwIn19fQ=====";

    @Serializable(headTexture = MATERIAL_HEAD, fromList = "materialList", fromListMapper = "materialListMapper")
    @SetterMappable(setterMethod = "setType")
    private Material type;
    @Serializable(headTexture = AMOUNT_HEAD)
    @SetterMappable(setterMethod = "setAmount")
    private int amount;
    @Serializable(headTexture = META_HEAD)
    private ItemMetaWrapper meta;

    public ItemStackWrapper(ItemStack wrapped) {
        super(wrapped);
        this.type = wrapped.getType();
        this.amount = wrapped.getAmount();
        this.meta = new ItemMetaWrapper(wrapped.getItemMeta());
    }

    public PluginInventoryHolder getCustomClickConsumer(JavaPlugin plugin, PluginInventoryHolder parent,
                                                        ModelPath<? extends Model> path, Field field, InventoryClickEvent e) {
        try {
            if (e.getClick().equals(ClickType.LEFT)) {
                if (!e.getCursor().getType().equals(Material.AIR)) {
                    ItemStack newItem = e.getCursor().clone();
                    this.wrapped = newItem;
                    this.amount = newItem.getAmount();
                    this.type = newItem.getType();
                    this.meta = new ItemMetaWrapper(newItem.getItemMeta());
                    path.saveModel();
                    e.getWhoClicked().getInventory().addItem(newItem);
                    e.getCursor().setAmount(0);
                    return parent;
                }
                path.addModel(this);
                return new ModelObjectInventoryHolder(plugin, parent, path);
            } else if (e.getClick().equals(ClickType.RIGHT)) {
                return new ModelCreateInventoryHolder(plugin, parent, path, field);
            }
        } catch (Exception e1) {
        }
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = wrapped.serialize();
        map.put("meta", meta);
        return map;
    }

    public static ItemStackWrapper deserialize(Map<String, Object> map) {
        ItemStack wrapped = ItemStack.deserialize(map);
        ItemMetaWrapper metaWrapper = (ItemMetaWrapper) map.getOrDefault("meta", null);
        wrapped.setItemMeta(metaWrapper != null ? metaWrapper.wrapped : null);
        ItemStackWrapper wrapper = new ItemStackWrapper(wrapped);
        return wrapper;
    }

    public static List<Object> materialList(ModelPath<?> path) {
        return Arrays.stream(Material.values()).filter(m -> !m.name().toLowerCase().contains("legacy") && m.isItem()).collect(Collectors.toList());
    }

    public static Function<Object, ItemStack> materialListMapper() {
        return obj -> {
            Material m = (Material) obj;
            return new ItemStack(m);
        };
    }

    @Override
    public ItemStack getGUIItem() {
        return null;
    }

}
