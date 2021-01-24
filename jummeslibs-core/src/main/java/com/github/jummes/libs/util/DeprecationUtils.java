package com.github.jummes.libs.util;

import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.model.wrapper.ItemMetaWrapper;
import com.github.jummes.libs.model.wrapper.LocationWrapper;
import com.github.jummes.libs.model.wrapper.VectorWrapper;
import com.google.gson.JsonSyntaxException;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @Deprecated
    public static String getLegacyString(String string) {
        try {
            BaseComponent[] component = ComponentSerializer.parse(string);
            component = Arrays.stream(component).filter(Objects::nonNull).toArray(BaseComponent[]::new);
            return getColoredString(BaseComponent.toLegacyText(component));
        } catch (JsonSyntaxException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getColoredString(string);
    }

    @Deprecated
    public static ItemMeta fixJsonItemMeta(ItemMeta meta) {
        if (meta == null) {
            return null;
        }

        meta.setDisplayName(getLegacyString(meta.getDisplayName()));
        if (meta.getLore() != null) {
            meta.setLore(meta.getLore().stream().map(DeprecationUtils::getLegacyString).collect(Collectors.toList()));
        }

        return meta;
    }

    @Deprecated
    @SneakyThrows
    public static String getColoredString(String value) {
        char[] arr = value.toCharArray();
        Pattern pattern = Pattern.compile("\\?[\\d\\w]");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            arr[matcher.start()] = '§';
        }
        value = new String(arr);
        return value;
    }
}
