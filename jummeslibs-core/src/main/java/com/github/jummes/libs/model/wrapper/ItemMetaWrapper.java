package com.github.jummes.libs.model.wrapper;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.jummes.libs.annotation.GUINameable;
import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.annotation.SetterMappable;
import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.model.Model;

import lombok.ToString;

@ToString
@GUINameable(GUIName = "Meta")
public class ItemMetaWrapper extends ModelWrapper<ItemMeta> implements Model {

    private final static String NAME_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDU4NGNmN2Q3OWYxYWViMjU1NGMxYmZkNDZlNmI3OGNhNmFlM2FhMmEyMTMyMzQ2YTQxMGYxNWU0MjZmMzEifX19";
    private final static String LORE_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzAzMDgyZjAzM2Y5NzI0Y2IyMmZlMjdkMGRlNDk3NTA5MDMzNTY0MWVlZTVkOGQ5MjdhZGY1YThiNjdmIn19fQ==";

    @Serializable(headTexture = NAME_HEAD)
    @SetterMappable(setterMethod = "setDisplayName")
    private String displayName;
    @Serializable(headTexture = LORE_HEAD)
    @SetterMappable(setterMethod = "setLore")
    private List<String> lore;

    public ItemMetaWrapper(ItemMeta wrapped) {
        super(wrapped);
        this.displayName = wrapped.getDisplayName();
        this.lore = wrapped.getLore() == null ? new ArrayList<String>() : wrapped.getLore();
    }

    @Override
    public Map<String, Object> serialize() {
        return wrapped.serialize();
    }

    public static ItemMetaWrapper deserialize(Map<String, Object> map) {
        try {
            Constructor<?> cons = Libs.getWrapper().getCraftMetaItemClass().getDeclaredConstructor(Map.class);
            cons.setAccessible(true);
            ItemMeta meta = (ItemMeta) cons.newInstance(map);
            return new ItemMetaWrapper(meta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ItemStack getGUIItem() {
        return null;
    }

}
