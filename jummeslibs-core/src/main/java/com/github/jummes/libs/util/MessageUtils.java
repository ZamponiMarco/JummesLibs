package com.github.jummes.libs.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

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