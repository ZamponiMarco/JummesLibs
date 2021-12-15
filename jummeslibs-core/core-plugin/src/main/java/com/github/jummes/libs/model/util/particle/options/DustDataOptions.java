package com.github.jummes.libs.model.util.particle.options;

import com.github.jummes.libs.annotation.Enumerable;
import com.github.jummes.libs.annotation.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;

import java.util.Map;

@AllArgsConstructor
@Enumerable.Child
@Getter
@Setter
public class DustDataOptions extends ParticleOptions {

    private static final String HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmIyYzI2NzhlOTM2NDA5ODhlNjg1YWM4OTM0N2VmYTQ1MjQxMTljOWQ4ZjcyNzhjZTAxODFiYzNlNGZiMmIwOSJ9fX0=";

    @Serializable(headTexture = HEAD)
    private String hexColor;
    @Serializable(headTexture = HEAD)
    @Serializable.Number(minValue = 0)
    private double size;

    public DustDataOptions() {
        this("ffbb00", 2.0);
    }

    public static DustDataOptions deserialize(Map<String, Object> map) {
        String hexColor = (String) map.get("hexColor");
        double size = (double) map.get("size");
        return new DustDataOptions(hexColor, size);
    }

    @Override
    public Object buildData() {
        return new org.bukkit.Particle.DustOptions(hex2Rgb(hexColor), (float) size);
    }

    @Override
    public ParticleOptions clone() {
        return new DustDataOptions(hexColor, size);
    }

    public Color hex2Rgb(String colorStr) {
        return Color.fromRGB(
                Integer.valueOf(colorStr.substring(0, 2), 16),
                Integer.valueOf(colorStr.substring(2, 4), 16),
                Integer.valueOf(colorStr.substring(4, 6), 16));
    }
}
