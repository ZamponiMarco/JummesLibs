package com.github.jummes.libs.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class ReflectUtils {

    public static List<Field> getFieldsList(Object target) {
        List<Field> fields = Lists.newArrayList(target.getClass().getDeclaredFields());
        ClassUtils.getAllSuperclasses(target.getClass()).forEach(
                superClass -> fields.addAll(0, Lists.newArrayList(((Class<?>) superClass).getDeclaredFields())));
        return fields;
    }

    public static List<Annotation> getAnnotationsList(Object target) {
        List<Annotation> annotations = Lists.newArrayList(target.getClass().getAnnotations());
        ClassUtils.getAllSuperclasses(target.getClass()).forEach(
                superClass -> annotations.addAll(0, Lists.newArrayList(((Class<?>) superClass).getAnnotations())));
        return annotations;
    }

    public static Field getField(Object target, String fieldName) {
        return getFieldsList(target).stream().filter(f -> f.getName().equalsIgnoreCase(fieldName)).findFirst()
                .orElse(null);
    }

}
