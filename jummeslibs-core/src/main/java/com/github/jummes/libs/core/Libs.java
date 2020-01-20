package com.github.jummes.libs.core;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.listener.PluginInventoryHolderClickListener;
import com.github.jummes.libs.listener.StringSettingChangeChatListener;
import com.github.jummes.libs.wrapper.VersionWrapper;

import lombok.Getter;

public class Libs {

	private static final String PACKAGE_PREFIX = "com.github.jummes.libs.wrapper.VersionWrapper_";

	@Getter
	private static VersionWrapper wrapper;

	public static void initializeLibrary(JavaPlugin plugin) {
		if (wrapper == null) {
			setUpWrapper(plugin);
		}
		registerEvents(plugin);
	}

	private static void registerEvents(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(new PluginInventoryHolderClickListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new StringSettingChangeChatListener(plugin), plugin);
	}

	private static void setUpWrapper(JavaPlugin plugin) {
		String serverVersion = plugin.getServer().getClass().getPackage().getName();
		String version = serverVersion.substring(serverVersion.lastIndexOf('.') + 1);

		try {
			wrapper = (VersionWrapper) Class.forName(PACKAGE_PREFIX + version).getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
