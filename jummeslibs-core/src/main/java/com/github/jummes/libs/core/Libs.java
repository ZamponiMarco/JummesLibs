package com.github.jummes.libs.core;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.listener.PluginInventoryHolderClickListener;
import com.github.jummes.libs.listener.StringSettingChangeChatListener;
import com.github.jummes.libs.wrapper.VersionWrapper;

import lombok.Getter;

public class Libs {

	private static final String PACKAGE_PREFIX = "com.github.jummes.libs.wrapper.VersionWrapper_";
	
	private static Libs instance;
	private JavaPlugin plugin;

	@Getter
	private VersionWrapper wrapper;

	private Libs(JavaPlugin plugin) {
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(new PluginInventoryHolderClickListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new StringSettingChangeChatListener(plugin), plugin);
		setUpWrapper();
	}

	public static void initializeLibrary(JavaPlugin plugin) {
		if (instance == null) {
			instance = new Libs(plugin);
		}
	}

	public static Libs getInstance() {
		return instance;
	}

	private void setUpWrapper() {
		String serverVersion = plugin.getServer().getClass().getPackage().getName();
		String version = serverVersion.substring(serverVersion.lastIndexOf('.') + 1);

		try {
			wrapper = (VersionWrapper) Class.forName(PACKAGE_PREFIX + version)
					.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
