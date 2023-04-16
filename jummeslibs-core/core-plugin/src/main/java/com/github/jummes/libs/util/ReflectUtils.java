package com.github.jummes.libs.util;

import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtils {

    public static List<Field> getFieldsList(Object target) {
        List<Field> fields = Lists.newArrayList(target.getClass().getDeclaredFields());
        getAllSuperclasses(target.getClass()).forEach(
                superClass -> fields.addAll(0, Lists.newArrayList(superClass.getDeclaredFields())));
        return fields;
    }

    public static List<Annotation> getAnnotationsList(Object target) {
        List<Annotation> annotations = Lists.newArrayList(target.getClass().getAnnotations());
        getAllSuperclasses(target.getClass()).forEach(
                superClass -> annotations.addAll(0, Lists.newArrayList(superClass.getAnnotations())));
        return annotations;
    }

    public static Field getField(Object target, String fieldName) {
        return getFieldsList(target).stream().filter(f -> f.getName().equalsIgnoreCase(fieldName)).findFirst()
                .orElse(null);
    }

    public static List<Class<?>> getAllSuperclasses(Class<?> clazz) {
        List<Class<?>> superclasses = new ArrayList<>();
        Class<?> currentClass = clazz.getSuperclass();
        while (currentClass != null) {
            superclasses.add(currentClass);
            currentClass = currentClass.getSuperclass();
        }
        return superclasses;
    }

    public static <T> Object readField(Field field, T obj) throws IllegalAccessException {
        boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        Object value = field.get(obj);
        field.setAccessible(isAccessible);
        return value;
    }

    public static <T> Constructor<T> getAccessibleConstructor(final Class<T> cls,
                                                              final Class<?>... parameterTypes) {
        try {
            return cls.getConstructor(parameterTypes);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    public static boolean isAssignable(Class<?> clazz1, Class<?> clazz2) {
        return clazz2.isAssignableFrom(clazz1);
    }

    public static Class<?> primitiveToWrapper(Class<?> primitiveClass) {
        if (primitiveClass == boolean.class) {
            return Boolean.class;
        } else if (primitiveClass == byte.class) {
            return Byte.class;
        } else if (primitiveClass == char.class) {
            return Character.class;
        } else if (primitiveClass == double.class) {
            return Double.class;
        } else if (primitiveClass == float.class) {
            return Float.class;
        } else if (primitiveClass == int.class) {
            return Integer.class;
        } else if (primitiveClass == long.class) {
            return Long.class;
        } else if (primitiveClass == short.class) {
            return Short.class;
        } else {
            throw new IllegalArgumentException("Not a primitive class: " + primitiveClass);
        }
    }

    public static void writeField(Field field, Object object, Object value) throws IllegalAccessException {
        boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(isAccessible);
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
