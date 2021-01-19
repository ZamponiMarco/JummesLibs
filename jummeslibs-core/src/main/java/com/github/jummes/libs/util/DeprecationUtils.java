package com.github.jummes.libs.util;

import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.model.wrapper.ItemMetaWrapper;
import com.github.jummes.libs.model.wrapper.LocationWrapper;
import com.github.jummes.libs.model.wrapper.VectorWrapper;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * All the deprecated static code is kept in here, planned to be deleted after two months after 1.17 come out
 */
public class DeprecationUtils {
    @Deprecated
    public static VectorWrapper handleOldVector(Map<String, Object> map) {
        try {
            return new VectorWrapper(Vector.deserialize(map));
        } catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public static LocationWrapper handleOldLocation(Map<String, Object> map) {
        try {
            return new LocationWrapper(Location.deserialize(map));
        } catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public static ItemStack handleOldItemStack(Map<String, Object> map) {
        ItemStack wrapped;
        wrapped = ItemStack.deserialize(map);

        ItemMetaWrapper meta = (ItemMetaWrapper) map.getOrDefault("meta", null);

        if (meta != null) {
            wrapped.setItemMeta(meta.getWrapped());
        }

        return wrapped;
    }

    @Deprecated
    public static ItemMetaWrapper handleOldItemMeta(Map<String, Object> map) {
        try {
            return new ItemMetaWrapper(Libs.getWrapper().deserializeItemMeta(map));
        } catch (Exception e) {
            return null;
        }
    }
}
