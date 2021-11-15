package com.github.jummes.libs.model.wrapper;

import com.github.jummes.libs.model.Model;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import javax.tools.DocumentationTool.Location;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public abstract class ModelWrapper<T> implements Model {

    @EqualsAndHashCode.Include
    protected T wrapped;

    public ModelWrapper(T wrapped) {
        this.wrapped = wrapped;
    }

    public static Map<Class<?>, Class<? extends ModelWrapper<?>>> getWrappers() {
        Map<Class<?>, Class<? extends ModelWrapper<?>>> map = new HashMap<>();
        map.put(Location.class, LocationWrapper.class);
        map.put(ItemStack.class, ItemStackWrapper.class);
        map.put(ItemMeta.class, ItemMetaWrapper.class);
        return map;
    }

}
