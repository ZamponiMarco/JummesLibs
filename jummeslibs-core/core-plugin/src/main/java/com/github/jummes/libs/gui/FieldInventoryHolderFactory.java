package com.github.jummes.libs.gui;

import com.github.jummes.libs.annotation.CustomClickable;
import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.gui.model.ModelCollectionInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.model.ObjectCollectionInventoryHolder;
import com.github.jummes.libs.gui.model.create.ModelCreateInventoryHolderFactory;
import com.github.jummes.libs.gui.setting.FieldChangeInventoryHolder;
import com.github.jummes.libs.gui.setting.FromListFieldChangeInventoryHolder;
import com.github.jummes.libs.gui.setting.change.ChangeInformation;
import com.github.jummes.libs.gui.setting.change.CollectionChangeInformation;
import com.github.jummes.libs.gui.setting.change.FieldChangeInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.model.wrapper.ModelWrapper;
import com.github.jummes.libs.util.MapperUtils;
import com.github.jummes.libs.util.ReflectUtils;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FieldInventoryHolderFactory {

    public static Map<Class<?>, Class<? extends ModelCollectionInventoryHolder<?>>> collectionGUIMap = new HashMap<>();

    public static PluginInventoryHolder createFieldInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
                                                                   ModelPath<? extends Model> path, Field field, InventoryClickEvent e) {
        try {
            Class<?> clazz = field.getType();

            /*
             * Is null
             */
            if (ReflectUtils.readField(field, path.getLast()) == null) {
                ChangeInformation changeInformation = new FieldChangeInformation(field);
                changeInformation.setValue(path, field.getType().getConstructor().newInstance());
                return parent;
            }

            /*
             * Choose from a list
             */
            if (field.isAnnotationPresent(Serializable.class)
                    && !field.getAnnotation(Serializable.class).fromList().equals("")) {
                List<Object> objects = (List<Object>) path.getLast().getClass()
                        .getMethod(field.getAnnotation(Serializable.class).fromList(), ModelPath.class)
                        .invoke(null, path);
                Function<Object, ItemStack> mapper = field.getAnnotation(Serializable.class).fromListMapper().equals("")
                        ? null
                        : (Function<Object, ItemStack>) path.getLast().getClass()
                        .getMethod(field.getAnnotation(Serializable.class).fromListMapper()).invoke(null);
                return new FromListFieldChangeInventoryHolder(plugin, parent, path, new FieldChangeInformation(field),
                        1, objects, mapper);
            }
            /*
             * Is a boolean
             */
            else if (clazz.equals(boolean.class)) {
                ChangeInformation changeInformation = new FieldChangeInformation(field);
                boolean current = (boolean) changeInformation.getValue(path);
                changeInformation.setValue(path, !current);
                return parent;
            }
            /*
             * Is a primitive type
             */
            else if (FieldChangeInventoryHolder.getInventories().containsKey(clazz)) {
                return FieldChangeInventoryHolder.getInventories().get(clazz)
                        .getConstructor(JavaPlugin.class, PluginInventoryHolder.class, ModelPath.class,
                                ChangeInformation.class)
                        .newInstance(plugin, parent, path, new FieldChangeInformation(field));
            }
            /*
             * Is a model wrapper
             */
            else if (ModelWrapper.getWrappers().containsKey(clazz)) {
                path.addModel(ModelWrapper.getWrappers().get(clazz).getConstructor(clazz)
                        .newInstance(ReflectUtils.readField(field, path.getLast())));
                return new ModelObjectInventoryHolder(plugin, parent, path);
            }
            /*
             * Is a collection
             */
            else if (ReflectUtils.isAssignable(clazz, Collection.class)) {
                Class<?> containedClass = TypeToken.of(field.getGenericType()).resolveType(clazz.getTypeParameters()[0])
                        .getRawType();
                if (collectionGUIMap.containsKey(containedClass)) {
                    return collectionGUIMap.get(containedClass).getConstructor(JavaPlugin.class, PluginInventoryHolder.class,
                            ModelPath.class, Field.class, int.class, Predicate.class).newInstance(plugin, parent, path,
                            field, 1, Predicates.alwaysTrue());
                } else if (ReflectUtils.isAssignable(containedClass, Model.class)) {
                    return new ModelCollectionInventoryHolder<>(plugin, parent, path, field, 1, obj -> true);
                } else {
                    return new ObjectCollectionInventoryHolder(plugin, parent, path, field, 1);
                }
            }
            /*
             * Is a model
             */
            else if (Model.class.isAssignableFrom(clazz)) {
                if (clazz.isAnnotationPresent(CustomClickable.class)
                        && !clazz.getAnnotation(CustomClickable.class).customFieldClickConsumer().equals("")) {
                    return (PluginInventoryHolder) clazz
                            .getMethod(clazz.getAnnotation(CustomClickable.class).customFieldClickConsumer(),
                                    JavaPlugin.class, PluginInventoryHolder.class, ModelPath.class, Field.class,
                                    InventoryClickEvent.class)
                            .invoke(ReflectUtils.readField(field, path.getLast()), plugin, parent,
                                    path, field, e);
                }
                if (e.getClick().equals(ClickType.LEFT)) {
                    path.addModel((Model) ReflectUtils.readField(field, path.getLast()));
                    return new ModelObjectInventoryHolder(plugin, parent, path);
                } else if (e.getClick().equals(ClickType.RIGHT)) {
                    return ModelCreateInventoryHolderFactory.create(plugin, parent, path, field);
                }
            }
            /*
             * Is an enum
             */
            else if (clazz.isEnum()) {
                return new FromListFieldChangeInventoryHolder(plugin, parent, path, new FieldChangeInformation(field),
                        1, Lists.newArrayList(clazz.getEnumConstants()), null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return parent;
    }

    public static PluginInventoryHolder createFieldInCollectionInventoryHolder(JavaPlugin plugin,
                                                                               PluginInventoryHolder parent, ModelPath<? extends Model> path, Field field, Object currentValue) {
        Class<?> clazz = field.getType();
        Class<?> containedClass = TypeToken.of(field.getGenericType()).resolveType(clazz.getTypeParameters()[0])
                .getRawType();
        try {
            if (FieldChangeInventoryHolder.getInventories().containsKey(containedClass)) {
                return FieldChangeInventoryHolder.getInventories().get(containedClass)
                        .getConstructor(JavaPlugin.class, PluginInventoryHolder.class, ModelPath.class,
                                ChangeInformation.class)
                        .newInstance(plugin, parent, path, new CollectionChangeInformation(field, currentValue));
            } else if (containedClass.isEnum()) {
                Function<Object, ItemStack> mapper;
                List<Object> objects;
                if (containedClass.equals(Material.class)) {
                    mapper = MapperUtils.getMaterialMapper();
                    objects = MapperUtils.getMaterialList();
                } else if (containedClass.equals(EquipmentSlot.class)) {
                    mapper = MapperUtils.getEquipmentSlotMapper();
                    objects = Lists.newArrayList( (Object[]) EquipmentSlot.values());
                } else if (containedClass.equals(EntityType.class)) {
                    mapper = MapperUtils.getMaterialMapper();
                    objects = Arrays.stream(EntityType.values()).filter(EntityType::isSpawnable).collect(Collectors.toList());
                } else {
                    mapper = null;
                    objects = Arrays.stream(containedClass.getEnumConstants()).collect(Collectors.toList());
                }
                return new FromListFieldChangeInventoryHolder(plugin, parent, path,
                        new CollectionChangeInformation(field, currentValue), 1, objects, mapper);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parent;
    }

}
