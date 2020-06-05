package com.github.jummes.libs.util;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang.ClassUtils;

import com.google.common.collect.Lists;

public class ReflectUtils {

    public static List<Field> getFieldsList(Object target) {
        List<Field> fields = Lists.newArrayList(target.getClass().getDeclaredFields());
        ClassUtils.getAllSuperclasses(target.getClass()).forEach(
                superClass -> fields.addAll(0, Lists.newArrayList(((Class<?>) superClass).getDeclaredFields())));
        return fields;
    }

    public static Field getField(Object target, String fieldName) {
        return getFieldsList(target).stream().filter(f -> f.getName().equalsIgnoreCase(fieldName)).findFirst()
                .orElse(null);
    }

}
