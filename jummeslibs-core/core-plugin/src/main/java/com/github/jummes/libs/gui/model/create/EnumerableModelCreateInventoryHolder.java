package com.github.jummes.libs.gui.model.create;

import com.github.jummes.libs.annotation.Enumerable;
import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.InjectUtils;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EnumerableModelCreateInventoryHolder extends CreateInventoryHolder {

    private final static String ITEM_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzAzMDgyZjAzM2Y5NzI0Y2IyMmZlMjdkMGRlNDk3NTA5MDMzNTY0MWVlZTVkOGQ5MjdhZGY1YThiNjdmIn19fQ==";

    private final PluginInventoryHolder parentCategory;

    public EnumerableModelCreateInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
                                                ModelPath<? extends Model> path, Field field,
                                                Class<? extends Model> modelClass, boolean isCollection) {
        this(plugin, parent, path, field, modelClass, isCollection, parent);
    }

    public EnumerableModelCreateInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
                                                ModelPath<? extends Model> path, Field field,
                                                Class<? extends Model> modelClass, boolean isCollection,
                                                PluginInventoryHolder parentCategory) {
        super(plugin, parent, path, field, modelClass, isCollection);
        this.parentCategory = parentCategory;
    }

    @Override
    protected void initializeInventory() {
        this.inventory = Bukkit.createInventory(this, 27,
                MessageUtils.color("&6Create a &c&l" + modelClass.getSimpleName()));

        List<Class<? extends Model>> injectedList = InjectUtils.getInjectionMap().getOrDefault(modelClass,
                Lists.newArrayList());

        List<Class<? extends Model>> classes = Stream.concat(Arrays.stream(modelClass.
                getAnnotation(Enumerable.Parent.class).classArray()), injectedList.stream()).
                filter(clazz -> (clazz.isAnnotationPresent(Enumerable.Child.class)
                        || clazz.isAnnotationPresent(Enumerable.Parent.class))
                        && isConditionSatisfied(clazz)).toList();

        int[] positions = getItemPositions(classes.size());

        IntStream.range(0, classes.size()).forEach(i -> {
            int position = positions[i];
            Class<? extends Model> clazz = classes.get(i);
            if (clazz.isAnnotationPresent(Enumerable.Child.class)) {
                registerClickConsumer(position,
                        getEnumerableItem(clazz),
                        getModelCreateConsumer(clazz, isCollection));
            } else if (clazz.isAnnotationPresent(Enumerable.Parent.class)) {
                registerClickConsumer(position,
                        getEnumerableItem(clazz),
                        getEnumerableModelConsumer(clazz));
            }
        });
        registerClickConsumer(26, getBackItem(), getBackConsumer());
        fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
    }

    private boolean isConditionSatisfied(Class<? extends Model> clazz) {
        try {
            return !(clazz.isAnnotationPresent(Enumerable.Displayable.class)
                    && !clazz.getAnnotation(Enumerable.Displayable.class).condition().equals(""))
                    || (boolean) clazz.getMethod(clazz.getAnnotation(Enumerable.Displayable.class).condition(),
                    ModelPath.class).invoke(null, path);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Consumer<InventoryClickEvent> getEnumerableModelConsumer(Class<? extends Model> clazz) {
        return e -> {
            e.getWhoClicked().openInventory(new EnumerableModelCreateInventoryHolder(plugin, parent, path, field,
                    clazz, isCollection, this).getInventory());
        };
    }


    private ItemStack getEnumerableItem(Class<?> clazz) {
        String name = clazz.isAnnotationPresent(Enumerable.Displayable.class)
                && !clazz.getAnnotation(Enumerable.Displayable.class).name().equals("")
                ? clazz.getAnnotation(Enumerable.Displayable.class).name()
                : "&6new &c&l" + clazz.getSimpleName();
        Component nameComponent = MessageUtils.color(name);
        List<Component> lore = clazz.isAnnotationPresent(Enumerable.Displayable.class)
                && !clazz.getAnnotation(Enumerable.Displayable.class).description().equals("")
                ? Libs.getLocale().getList(clazz.getAnnotation(Enumerable.Displayable.class).description())
                : new ArrayList<>();
        ItemStack item = clazz.isAnnotationPresent(Enumerable.Displayable.class)
                && !clazz.getAnnotation(Enumerable.Displayable.class).headTexture().equals("")
                ? Libs.getVersionWrapper().skullFromValue(clazz.getAnnotation(Enumerable.Displayable.class).headTexture())
                : Libs.getVersionWrapper().skullFromValue(ITEM_HEAD);
        return ItemUtils.getNamedItem(item, nameComponent, lore);
    }

    protected Consumer<InventoryClickEvent> getBackConsumer() {
        return e -> {
            if (parentCategory != null) {
                e.getWhoClicked().openInventory(parentCategory.getInventory());
            } else {
                e.getWhoClicked().closeInventory();
            }
        };
    }
}
