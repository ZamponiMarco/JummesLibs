package com.github.jummes.libs.model.wrapper;

import com.github.jummes.libs.annotation.Serializable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class VectorWrapper extends ModelWrapper<Vector> implements Cloneable {

    private static final String X_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkxZDZlZGE4M2VkMmMyNGRjZGNjYjFlMzNkZjM2OTRlZWUzOTdhNTcwMTIyNTViZmM1NmEzYzI0NGJjYzQ3NCJ9fX0=";
    private static final String Y_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODlmZjhjNzQ0OTUwNzI5ZjU4Y2I0ZTY2ZGM2OGVhZjYyZDAxMDZmOGE1MzE1MjkxMzNiZWQxZDU1ZTMifX19=";
    private static final String Z_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzA1ZjE4ZDQxNmY2OGU5YmQxOWQ1NWRmOWZhNzQyZWRmYmYxYTUyNWM4ZTI5ZjY1OWFlODMzYWYyMTdkNTM1In19fQ===";
    @Serializable(headTexture = Y_HEAD)
    private double y;
    @Serializable(headTexture = X_HEAD)
    private double x;
    @Serializable(headTexture = Z_HEAD)
    private double z;

    public VectorWrapper() {
        this(new Vector(0, 0, 0));
    }

    public VectorWrapper(Vector wrapped) {
        super(wrapped);
        this.x = wrapped.getX();
        this.y = wrapped.getY();
        this.z = wrapped.getZ();
    }

    public static VectorWrapper deserialize(Map<String, Object> map) {
        Map<String, Object> vectorMap = (Map<String, Object>) map.get("vector");
        if (vectorMap == null) {
            throw new NullPointerException();
        }
        return new VectorWrapper(Vector.deserialize(vectorMap));
    }

    @Override
    public void onModify(Field field) {
        if (field.getDeclaringClass().equals(LocationWrapper.class)) {
            switch (field.getName()) {
                case "x":
                    wrapped.setX(x);
                    break;
                case "y":
                    wrapped.setY(y);
                    break;
                case "z":
                    wrapped.setZ(z);
                    break;
            }
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("==", getClass().getName());
        map.put("vector", wrapped.serialize());
        return map;
    }

    @Override
    public String toString() {
        NumberFormat f = new DecimalFormat("#0.00");
        return String.format("&6&lVector [&c%s&6/&c%s&6/&c%s&6&l]", f.format(x), f.format(y), f.format(z));
    }

    @Override
    public VectorWrapper clone() {
        return new VectorWrapper(new Vector(x, y, z));
    }
}
