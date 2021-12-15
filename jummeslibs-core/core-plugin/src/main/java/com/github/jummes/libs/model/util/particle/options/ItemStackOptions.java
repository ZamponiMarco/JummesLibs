package com.github.jummes.libs.model.util.particle.options;

import com.github.jummes.libs.annotation.Enumerable;
import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.model.wrapper.ItemStackWrapper;
import com.github.jummes.libs.util.MapperUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
@Enumerable.Child
@Getter
@Setter
public class ItemStackOptions extends ParticleOptions {

    private static final String HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJiMzViZGE1ZWJkZjEzNWY0ZTcxY2U0OTcyNmZiZWM1NzM5ZjBhZGVkZjAxYzUxOWUyYWVhN2Y1MTk1MWVhMiJ9fX0=";

    @Serializable(headTexture = HEAD, stringValue = true, fromListMapper = "materialListMapper", fromList = "materialList")
    private Material item;

    public ItemStackOptions() {
        this(Material.IRON_AXE);
    }

    public static ItemStackOptions deserialize(Map<String, Object> map) {
        Material item;
        try {
            item = Material.valueOf((String) map.get("item"));
        } catch (Exception e) {
            item = ((ItemStackWrapper) map.get("item")).getWrapped().getType();
        }
        return new ItemStackOptions(item);
    }

    public static List<Object> materialList(ModelPath<?> path) {
        return MapperUtils.getMaterialList();
    }

    public static Function<Object, ItemStack> materialListMapper() {
        return MapperUtils.getMaterialMapper();
    }

    @Override
    public Object buildData() {
        return new ItemStack(item);
    }

    @Override
    public ParticleOptions clone() {
        return new ItemStackOptions(item);
    }
}
