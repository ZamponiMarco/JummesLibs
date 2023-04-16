package com.github.jummes.libs.model.math;

import com.github.jummes.libs.annotation.GUINameable;
import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.model.Model;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@GUINameable(GUIName = "toString")
public class IntRange implements Model {

    private static final String MIN_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkxMmQ0NWIxYzc4Y2MyMjQ1MjcyM2VlNjZiYTJkMTU3NzdjYzI4ODU2OGQ2YzFiNjJhNTQ1YjI5YzcxODcifX19=";
    private static final String MAX_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk5YWFmMjQ1NmE2MTIyZGU4ZjZiNjI2ODNmMmJjMmVlZDlhYmI4MWZkNWJlYTFiNGMyM2E1ODE1NmI2NjkifX19=";

    @Serializable(headTexture = MIN_HEAD)
    private int min;
    @Serializable(headTexture = MAX_HEAD)
    private int max;

    public IntRange() {
        this(1, 1);
    }

    public static IntRange deserialize(Map<String, Object> map) {
        int min = (int) map.get("min");
        int max = (int) map.get("max");
        return new IntRange(min, max);
    }

    public int getDifference() {
        return max - min;
    }

    @Override
    public String toString() {
        return String.format("min=%d, max=%d", min, max);
    }

}
