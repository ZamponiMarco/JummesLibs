package com.github.jummes.libs.gui.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.annotation.CustomClickable;
import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.create.ModelCreateInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelManager;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;

public class ModelCollectionInventoryHolder extends ModelObjectInventoryHolder {

    private static final String ADD_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZjNmRjZjczOWMxMWZlY2U0M2NkZDE4NGRlYTc5MWNmNzU3YmY3YmQ5MTUzNmZkYmM5NmZhNDdhY2ZiIn19fQ==";
    private static final String NEXT_PAGE_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTdiMDNiNzFkM2Y4NjIyMGVmMTIyZjk4MzFhNzI2ZWIyYjI4MzMxOWM3YjYyZTdkY2QyZDY0ZDk2ODIifX19==";
    private static final String PREVIOUS_PAGE_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDgzNDhhYTc3ZjlmYjJiOTFlZWY2NjJiNWM4MWI1Y2EzMzVkZGVlMWI5MDVmM2E4YjkyMDk1ZDBhMWYxNDEifX19==";

    private static final int MODELS_NUMBER = 50;

    protected int page;
    protected Field field;

    public ModelCollectionInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
                                          ModelPath<? extends Model> path, Field field, int page) {
        super(plugin, parent, path);
        this.field = field;
        this.page = page;
    }

    @SuppressWarnings("rawtypes")
    public ModelCollectionInventoryHolder(JavaPlugin plugin, ModelManager<? extends Model> manager, String fieldName)
            throws NoSuchFieldException, SecurityException {
        this(plugin, null, new ModelPath(manager, null), manager.getClass().getDeclaredField(fieldName), 1);
    }

    @Override
    protected void initializeInventory() {
        try {
            List<Model> models = ((Collection<Model>) FieldUtils.readField(field,
                    path.getLast() != null ? path.getLast() : path.getModelManager(), true)).stream()
                    .filter(model -> model.getGUIItem() != null).collect(Collectors.toList());
            List<Model> toList = models.stream().filter(model -> models.indexOf(model) >= (page - 1) * MODELS_NUMBER
                    && models.indexOf(model) <= page * MODELS_NUMBER - 1).collect(Collectors.toList());
            int maxPage = (int) Math.ceil((models.size() > 0 ? models.size() : 1) / (double) MODELS_NUMBER);

            this.inventory = Bukkit.createInventory(this, 54,
                    MessageUtils.color("&6Collection of &c&l" + field.getName() + " &6&l(&c" + page + "&6&l/&c" + maxPage + "&6&l)"));

            toList.forEach(model -> {
                registerClickConsumer(toList.indexOf(model), model.getGUIItem(), e -> {
                    try {
                        if (model.getClass().isAnnotationPresent(CustomClickable.class) && !model.getClass()
                                .getAnnotation(CustomClickable.class).customCollectionClickConsumer().equals("")) {
                            model.getClass()
                                    .getMethod(
                                            model.getClass().getAnnotation(CustomClickable.class)
                                                    .customCollectionClickConsumer(),
                                            JavaPlugin.class, PluginInventoryHolder.class, ModelPath.class, Field.class,
                                            InventoryClickEvent.class)
                                    .invoke(model, plugin, this, path, field, e);
                        } else {
                            defaultClickConsumer(model, e);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            });
            placeCollectionOnlyItems(maxPage);
            registerClickConsumer(53, getBackItem(), getBackConsumer());
            fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void placeCollectionOnlyItems(int maxPage) {
        registerClickConsumer(50, getAddItem(), e -> {
            e.getWhoClicked().openInventory(new ModelCreateInventoryHolder(plugin, this, path, field).getInventory());
        });
        if (page != maxPage) {
            registerClickConsumer(52,
                    ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(NEXT_PAGE_ITEM),
                            MessageUtils.color("&6&lNext page"), new ArrayList<String>()),
                    e -> e.getWhoClicked().openInventory(
                            new ModelCollectionInventoryHolder(plugin, parent, path, field, page + 1).getInventory()));
        }
        if (page != 1) {
            registerClickConsumer(51,
                    ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(PREVIOUS_PAGE_ITEM),
                            MessageUtils.color("&6&lPrevious page"), new ArrayList<String>()),
                    e -> e.getWhoClicked().openInventory(
                            new ModelCollectionInventoryHolder(plugin, parent, path, field, page - 1).getInventory()));
        }
    }

    private void defaultClickConsumer(Model model, InventoryClickEvent e) throws IllegalAccessException {
        if (e.getClick().equals(ClickType.LEFT)) {
            path.addModel(model);
            e.getWhoClicked().openInventory(new ModelObjectInventoryHolder(plugin, this, path).getInventory());
        } else if (e.getClick().equals(ClickType.RIGHT)) {
            ((Collection<Model>) FieldUtils.readField(field,
                    path.getLast() != null ? path.getLast() : path.getModelManager(), true)).remove(model);
            path.addModel(model);
            path.deleteModel();
            path.popModel();
            model.onRemoval();
            e.getWhoClicked().openInventory(getInventory());
        } else if (ClassUtils.isAssignable(model.getClass(), Cloneable.class)
                && e.getClick().equals(ClickType.MIDDLE)) {
            try {
                Method method = model.getClass().getDeclaredMethod("clone");
                method.setAccessible(true);
                ((Collection<Model>) FieldUtils.readField(field,
                        path.getLast() != null ? path.getLast() : path.getModelManager(), true))
                        .add((Model) method.invoke(model));
                e.getWhoClicked().openInventory(getInventory());
                path.saveModel();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private ItemStack getAddItem() {
        return ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(ADD_ITEM), MessageUtils.color("&6&lAdd"),
                new ArrayList<String>());
    }
}
