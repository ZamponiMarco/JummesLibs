package com.github.jummes.libs.model.util.particle.options;

import com.github.jummes.libs.annotation.Enumerable;
import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.MapperUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Enumerable.Child
@Getter
@Setter
public class BlockDataOptions extends ParticleOptions {

    private static final String HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTU0ODE4MjMzYzgxMTg3M2U4NWY1YTRlYTQ0MjliNzVmMjNiNmFlMGVhNmY1ZmMwZjdiYjQyMGQ3YzQ3MSJ9fX0=";

    @Serializable(headTexture = HEAD, stringValue = true, fromListMapper = "materialListMapper", fromList = "materialList")
    private Material material;

    public BlockDataOptions() {
        this(Material.STONE);
    }

    public static BlockDataOptions deserialize(Map<String, Object> map) {
        Material material = Material.valueOf((String) map.get("material"));
        return new BlockDataOptions(material);
    }

    public static List<Object> materialList(ModelPath<?> path) {
        return MapperUtils.getMaterialList().stream().filter(m -> ((Material) m).isBlock()).collect(Collectors.toList());
    }

    public static Function<Object, ItemStack> materialListMapper() {
        return MapperUtils.getMaterialMapper();
    }

    @Override
    public Object buildData() {
        return Bukkit.createBlockData(material);
    }

    @Override
    public ParticleOptions clone() {
        return new BlockDataOptions(material);
    }
}
