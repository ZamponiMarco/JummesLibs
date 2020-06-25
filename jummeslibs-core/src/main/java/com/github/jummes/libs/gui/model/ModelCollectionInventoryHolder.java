package com.github.jummes.libs.gui.model;

import com.github.jummes.libs.annotation.CustomClickable;
import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.create.ModelCreateInventoryHolderFactory;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelManager;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;
import com.github.jummes.libs.util.ReflectUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModelCollectionInventoryHolder<S extends Model> extends PluginInventoryHolder {

    private static final String ADD_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZjNmRjZjczOWMxMWZlY2U0M2NkZDE4NGRlYTc5MWNmNzU3YmY3YmQ5MTUzNmZkYmM5NmZhNDdhY2ZiIn19fQ==";
    private static final String NEXT_PAGE_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTdiMDNiNzFkM2Y4NjIyMGVmMTIyZjk4MzFhNzI2ZWIyYjI4MzMxOWM3YjYyZTdkY2QyZDY0ZDk2ODIifX19==";
    private static final String PREVIOUS_PAGE_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDgzNDhhYTc3ZjlmYjJiOTFlZWY2NjJiNWM4MWI1Y2EzMzVkZGVlMWI5MDVmM2E4YjkyMDk1ZDBhMWYxNDEifX19==";

    private static final int MODELS_NUMBER = 50;

    protected ModelPath<S> path;
    protected int page;
    protected Field field;
    protected Predicate<S> filter;

    public ModelCollectionInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
                                          ModelPath<S> path, Field field, int page, Predicate<S> filter) {
        super(plugin, parent);
        this.path = path;
        this.field = field;
        this.page = page;
        this.filter = filter;
    }

    public ModelCollectionInventoryHolder(JavaPlugin plugin, ModelManager<S> manager, String fieldName)
            throws NoSuchFieldException, SecurityException {
        this(plugin, null, new ModelPath<>(manager, null), manager.getClass().getDeclaredField(fieldName), 1, obj -> true);
    }

    public ModelCollectionInventoryHolder(JavaPlugin plugin, ModelManager<S> manager, String fieldName, Predicate<S> filter)
            throws NoSuchFieldException, SecurityException {
        this(plugin, null, new ModelPath<>(manager, null), manager.getClass().getDeclaredField(fieldName), 1, filter);
    }

    @Override
    protected void initializeInventory() {
        try {
            List<S> models = ((Collection<S>) FieldUtils.readField(field,
                    path.getLast() != null ? path.getLast() : path.getModelManager(), true)).stream()
                    .filter(model -> model.getGUIItem() != null && filter.test(model)).collect(Collectors.toList());
            List<S> toList = models.stream().filter(model -> models.indexOf(model) >= (page - 1) * MODELS_NUMBER
                    && models.indexOf(model) <= page * MODELS_NUMBER - 1).collect(Collectors.toList());
            int maxPage = (int) Math.ceil((models.size() > 0 ? models.size() : 1) / (double) MODELS_NUMBER);

            this.inventory = Bukkit.createInventory(this, 54,
                    MessageUtils.color("&6Collection of &c&l" + field.getName() + " &6&l(&c" + page + "&6&l/&c" + maxPage + "&6&l)"));

            toList.forEach(model -> {
                registerClickConsumer(toList.indexOf(model), model.getGUIItem(), e -> {
                    executeClickConsumer(model, e);
                });
            });
            placeCollectionOnlyItems(maxPage);
            registerClickConsumer(53, getBackItem(), getBackConsumer());
            fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If a custom clickable is present execute it, execute default one otherwise
     *
     * @param model
     * @param e
     */
    private void executeClickConsumer(S model, InventoryClickEvent e) {
        try {
            List<Annotation> annotations = ReflectUtils.getAnnotationsList(model);
            Annotation a = annotations.stream().filter(annotation ->
                    annotation instanceof CustomClickable
                            && !((CustomClickable) annotation).customCollectionClickConsumer().equals("")).
                    findFirst().orElse(null);
            if (a != null) {
                model.getClass()
                        .getMethod(
                                ((CustomClickable) a).customCollectionClickConsumer(),
                                JavaPlugin.class, PluginInventoryHolder.class, ModelPath.class, Field.class,
                                InventoryClickEvent.class)
                        .invoke(model, plugin, this, path, field, e);
            } else {
                defaultClickConsumer(model, e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void placeCollectionOnlyItems(int maxPage) {
        registerClickConsumer(50, getAddItem(), e -> {
            e.getWhoClicked().openInventory(
                    ModelCreateInventoryHolderFactory.create(plugin, this, path, field).getInventory());
        });
        if (page != maxPage) {
            registerClickConsumer(52,
                    ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(NEXT_PAGE_ITEM),
                            MessageUtils.color("&6&lNext page"), new ArrayList<>()),
                    e -> e.getWhoClicked().openInventory(
                            new ModelCollectionInventoryHolder<S>(plugin, parent, path, field, page + 1, filter).getInventory()));
        }
        if (page != 1) {
            registerClickConsumer(51,
                    ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(PREVIOUS_PAGE_ITEM),
                            MessageUtils.color("&6&lPrevious page"), new ArrayList<>()),
                    e -> e.getWhoClicked().openInventory(
                            new ModelCollectionInventoryHolder<S>(plugin, parent, path, field, page - 1, filter).getInventory()));
        }
    }

    private void defaultClickConsumer(S model, InventoryClickEvent e) throws IllegalAccessException {
        if (e.getClick().equals(ClickType.LEFT)) {
            path.addModel(model);
            e.getWhoClicked().openInventory(new ModelObjectInventoryHolder(plugin, this, path).getInventory());
        } else if (e.getClick().equals(ClickType.RIGHT)) {
            ((Collection<S>) FieldUtils.readField(field,
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
                ((Collection<S>) FieldUtils.readField(field,
                        path.getLast() != null ? path.getLast() : path.getModelManager(), true))
                        .add((S) method.invoke(model));
                e.getWhoClicked().openInventory(getInventory());
                path.saveModel();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private ItemStack getAddItem() {
        return ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(ADD_ITEM), MessageUtils.color("&6&lAdd"),
                new ArrayList<>());
    }
}
