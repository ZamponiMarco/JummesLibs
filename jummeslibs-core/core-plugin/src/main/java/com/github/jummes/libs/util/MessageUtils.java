package com.github.jummes.libs.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;
import java.util.Random;

public class MessageUtils {

    private static final ComponentSerializer<Component, TextComponent, String> serializer =
            LegacyComponentSerializer.legacyAmpersand();

    public static Component color(String string) {
        return Component.text().decoration(TextDecoration.ITALIC, false).append(serializer.deserialize(string)).build();
    }

    public static Component header(String string) {
        return color(String.format("&e=--- &c%s &e---=\n", string));
    }

    public static Component delimiter(String string) {
        return color("&e-------" + repeatChar('-', string.length()));
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String repeatChar(char c, int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    public static String getRandomString(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            char c = alphabet.charAt(index);
            sb.append(c);
        }
        return sb.toString();
    }

}