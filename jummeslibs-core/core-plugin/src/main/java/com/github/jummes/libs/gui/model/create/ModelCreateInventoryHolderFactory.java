package com.github.jummes.libs.gui.model.create;

import com.github.jummes.libs.annotation.Enumerable;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang.ClassUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Collection;

public class ModelCreateInventoryHolderFactory {

    public static CreateInventoryHolder create(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path,
                                               Field field) {
        boolean isCollection = ClassUtils.isAssignable(field.getType(), Collection.class);
        Class<? extends Model> model = getModelClassFromField(field, isCollection);
        return create(plugin, parent, path, field, model);
    }

    public static CreateInventoryHolder create(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path,
                                               Field field, Class<? extends Model> model) {
        boolean isCollection = ClassUtils.isAssignable(field.getType(), Collection.class);
        if (model.isAnnotationPresent(Enumerable.Parent.class)) {
            return new EnumerableModelCreateInventoryHolder(plugin, parent, path, field, model, isCollection);
        } else {
            return new ModelCreateInventoryHolder(plugin, parent, path, field, model, isCollection);
        }
    }

    private static Class<? extends Model> getModelClassFromField(Field field, boolean isCollection) {
        Class<?> fieldClass = field.getType();
        return isCollection
                ? (Class<Model>) TypeToken.of(field.getGenericType()).resolveType(fieldClass.getTypeParameters()[0])
                .getRawType()
                : (Class<Model>) fieldClass;
    }

}
