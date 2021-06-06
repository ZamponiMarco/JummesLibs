package com.github.jummes.libs.util;

import com.github.jummes.libs.model.Model;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InjectUtils {

    private static final Map<Class<? extends Model>, List<Class<? extends Model>>> injectionMap =
            new HashMap<>();

    public static Map<Class<? extends Model>, List<Class<? extends Model>>> getInjectionMap() {
        return injectionMap;
    }

    public static void injectClass(Class<? extends Model> parent, Class<? extends Model> child) {
        injectionMap.computeIfAbsent(parent, k -> Lists.newArrayList()).add(child);
    }
}
