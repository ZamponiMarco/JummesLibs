package com.github.jummes.libs.model.wrapper;

import com.github.jummes.libs.annotation.CustomClickable;
import com.github.jummes.libs.annotation.GUINameable;
import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.model.create.ModelCreateInventoryHolderFactory;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.MapperUtils;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ToString
@Setter
@CustomClickable(customFieldClickConsumer = "getCustomClickConsumer")
@GUINameable(GUIName = "getName")
public class ItemStackWrapper extends ModelWrapper<ItemStack> implements Cloneable {

    private static final boolean DEFAULT_NO_AMOUNT = true;
    private static final ItemStack DEFAULT_ITEM = new ItemStack(Material.STONE);

    private static final String AMOUNT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjdkYzNlMjlhMDkyM2U1MmVjZWU2YjRjOWQ1MzNhNzllNzRiYjZiZWQ1NDFiNDk1YTEzYWJkMzU5NjI3NjUzIn19fQ===";
    private static final String META_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4MzM0MTUxYzIzNGY0MTY0NzExM2JlM2VhZGYyODdkMTgxNzExNWJhYzk0NDVmZmJiYmU5Y2IyYjI4NGIwIn19fQ=====";

    @Serializable(displayItem = "getMaterialItem", fromList = "materialList", fromListMapper = "materialListMapper")
    private Material type;
    @Serializable(displayItem = "getAmountItem")
    @Serializable.Number(minValue = 1)
    private int amount;
    @Serializable(headTexture = META_HEAD)
    private ItemMetaWrapper meta;
    @Serializable
    @Serializable.Optional(defaultValue = "DEFAULT_NO_AMOUNT")
    private boolean noAmount;

    public ItemStackWrapper() {
        this(DEFAULT_NO_AMOUNT);
    }

    public ItemStackWrapper(boolean noAmount) {
        this(DEFAULT_ITEM.clone(), noAmount);
    }

    public ItemStackWrapper(@NonNull ItemStack wrapped, boolean noAmount) {
        super(wrapped);
        this.type = wrapped.getType();
        this.amount = wrapped.getAmount();
        this.meta = new ItemMetaWrapper(wrapped.getItemMeta());
        this.noAmount = noAmount;
    }

    public static ItemStackWrapper deserialize(Map<String, Object> map) {
        ItemStack wrapped = ItemStack.deserialize((Map<String, Object>) map.getOrDefault("item", DEFAULT_ITEM.clone()));
        boolean noAmount = (boolean) map.getOrDefault("noAmount", DEFAULT_NO_AMOUNT);

        return new ItemStackWrapper(wrapped, noAmount);
    }

    public static List<Object> materialList(ModelPath<?> path) {
        return MapperUtils.getMaterialList();
    }

    public static Function<Object, ItemStack> materialListMapper() {
        return MapperUtils.getMaterialMapper();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("==", getClass().getName());
        map.put("item", wrapped.serialize());
        map.put("noAmount", noAmount);
        return map;
    }

    @Override
    public void onModify(Field field) {
        Class<?> clazz = field.getDeclaringClass();
        if (clazz.equals(ItemMetaWrapper.class)) {
            this.wrapped.setItemMeta(this.meta.wrapped);
        } else if (clazz.equals(ItemStackWrapper.class)) {
            switch (field.getName()) {
                case "type":
                    wrapped.setType(type);
                    break;
                case "amount":
                    wrapped.setAmount(amount);
                    break;
            }
        }
    }

    public ItemStack getAmountItem() {
        if (!noAmount) {
            return Libs.getVersionWrapper().skullFromValue(AMOUNT_HEAD);
        }
        return null;
    }

    public ItemStack getMaterialItem() {
        return MapperUtils.getMaterialMapper().apply(type);
    }

    public String getName() {
        return "Item";
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
                return ModelCreateInventoryHolderFactory.create(plugin, parent, path, field);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public ItemStackWrapper clone() {
        return new ItemStackWrapper(wrapped.clone(), noAmount);
    }
}
