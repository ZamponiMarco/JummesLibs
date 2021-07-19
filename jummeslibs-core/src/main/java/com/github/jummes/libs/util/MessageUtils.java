package com.github.jummes.libs.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang.StringUtils;

public class MessageUtils {

    private static final ComponentSerializer<Component, TextComponent, String> serializer =
            LegacyComponentSerializer.legacyAmpersand();

    public static Component color(String string) {
        return serializer.deserialize(string);
    }

    public static Component header(String string) {
        return color(String.format("&e=--- &c%s &e---=\n", string));
    }

    public static Component delimiter(String string) {
        return color("&e-------" + StringUtils.repeat("-", string.length()));
    }

}