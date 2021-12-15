package com.github.jummes.libs.model.util.particle.options;

import com.github.jummes.libs.model.Model;
import lombok.SneakyThrows;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class ParticleOptions implements Model, Cloneable {

    @SneakyThrows
    public static ParticleOptions buildOptions(Class<?> clazz) {
        Class<? extends ParticleOptions> optionsClass = getParticleOptionsMap().get(clazz);
        if (optionsClass != null) {
            return optionsClass.getConstructor().newInstance();
        }
        return null;
    }

    public static Map<Class<?>, Class<? extends ParticleOptions>> getParticleOptionsMap() {
        Map<Class<?>, Class<? extends ParticleOptions>> map = new HashMap<>();
        map.put(org.bukkit.Particle.DustOptions.class, DustDataOptions.class);
        map.put(ItemStack.class, ItemStackOptions.class);
        map.put(BlockData.class, BlockDataOptions.class);
        return map;
    }

    public abstract Object buildData();

    public abstract ParticleOptions clone();

}
