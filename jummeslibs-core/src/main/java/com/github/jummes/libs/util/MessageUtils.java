package com.github.jummes.libs.util;

import com.github.jummes.libs.core.Libs;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    /**
     * Returns a string that represents a colored string
     *
     * @param string to be colored
     * @return colored string
     */
    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String header(String string) {
        return color(String.format("&e=--- &c%s &e---=\n", string));
    }

    public static String delimiter(String string) {
        return color("&e-------" + StringUtils.repeat("-", string.length()));
    }

}