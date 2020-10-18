package com.github.jummes.libs.model;

import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.util.ReflectUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a class that contains data, usually configured as a Java Bean
 * object, from this class data managers and databases can be built it has to be
 * configuration serializable
 *
 * @author Marco
 */
public interface Model extends ConfigurationSerializable {

    default ItemStack getGUIItem() {
        return null;
    }

    default void beforeComponentCreation(Class<? extends Model> modelClass) {
    }

    default void afterComponentCreation(Model model) {
    }

    default void beforeComponentSetting(Model model) {
    }

    default void afterComponentSetting(Model model) {
    }

    default void beforeModify() {
    }

    default void onModify(Field field) {
    }

    default void onCreation() {
    }

    default void onRemoval() {
    }

    default Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("==", getClass().getName());
        List<Field> fields = ReflectUtils.getFieldsList(this);
        fields.stream().filter(field -> field.isAnnotationPresent(Serializable.class)).forEach(field -> {
            try {
                Object value = FieldUtils.readField(field, this, true);
                Object defaultValue = null;
                if (field.isAnnotationPresent(Serializable.Optional.class)) {
                    Class toInspect = getClass();
                    Field defaultField;
                    while (defaultValue == null || toInspect != null) {
                        defaultField = FieldUtils.getDeclaredField(toInspect,
                                field.getAnnotation(Serializable.Optional.class).defaultValue(), true);
                        if (defaultField != null) {
                            defaultValue = FieldUtils.readStaticField(defaultField, true);
                        }
                        toInspect = toInspect.getSuperclass();
                    }
                }

                if (value != null && (defaultValue == null || !defaultValue.equals(value))) {
                    map.put(field.getName(),
                            field.getAnnotation(Serializable.class).stringValue() ? value.toString() : value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return map;
    }

}