package com.github.jummes.libs.model.wrapper;

import com.github.jummes.libs.annotation.GUINameable;
import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.util.DeprecationUtils;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@Setter
@GUINameable(GUIName = "getName")
public class ItemMetaWrapper extends ModelWrapper<ItemMeta> implements Cloneable {

    private final static String NAME_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDU4NGNmN2Q3OWYxYWViMjU1NGMxYmZkNDZlNmI3OGNhNmFlM2FhMmEyMTMyMzQ2YTQxMGYxNWU0MjZmMzEifX19";
    private final static String LORE_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzAzMDgyZjAzM2Y5NzI0Y2IyMmZlMjdkMGRlNDk3NTA5MDMzNTY0MWVlZTVkOGQ5MjdhZGY1YThiNjdmIn19fQ==";

    @Serializable(headTexture = NAME_HEAD)
    private String displayName;
    @Serializable(headTexture = LORE_HEAD)
    private List<String> lore;

    public ItemMetaWrapper(@NonNull ItemMeta wrapped) {
        super(wrapped);
        this.wrapped = DeprecationUtils.fixJsonItemMeta(wrapped);
        if (wrapped != null) {
            this.displayName = wrapped.getDisplayName();
            this.lore = wrapped.getLore() == null ? new ArrayList<>() : wrapped.getLore();
        } else {
            this.displayName = "";
            this.lore = new ArrayList<>();
        }
    }

    public static ItemMetaWrapper deserialize(Map<String, Object> map) {
        try {
            Map<String, Object> metaMap = (Map<String, Object>) map.get("meta");
            if (metaMap == null) {
                throw new NullPointerException();
            }
            return new ItemMetaWrapper(Libs.getWrapper().deserializeItemMeta(metaMap));
        } catch (Exception ignored) {
            return DeprecationUtils.handleOldItemMeta(map);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("==", getClass().getName());
        map.put("meta", wrapped.serialize());
        return map;
    }

    @Override
    public void onModify(Field field) {
        Class<?> clazz = field.getDeclaringClass();
        if (clazz.equals(ItemMetaWrapper.class)) {
            switch (field.getName()) {
                case "displayName":
                    wrapped.setDisplayName(displayName);
                    break;
                case "lore":
                    wrapped.setLore(lore);
                    break;
            }
        }
    }

    public String getName() {
        return "ItemMeta";
    }

    @Override
    public ItemStack getGUIItem() {
        return null;
    }

    @Override
    public ItemMetaWrapper clone() {
        return new ItemMetaWrapper(wrapped.clone());
    }
}
