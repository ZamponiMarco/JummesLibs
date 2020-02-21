package com.github.jummes.libs.localization;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.util.MessageUtils;
import com.google.common.collect.Lists;

public class PluginLocale {

	private final static String FOLDERNAME = "locale";
	private File dataFile;
	private YamlConfiguration config;

	public PluginLocale(JavaPlugin plugin, List<String> defaultLocales, String filename) {
		File folder = new File(plugin.getDataFolder(), FOLDERNAME);

		if (!folder.exists()) {
			folder.mkdir();
		}

		defaultLocales.forEach(localeString -> {
			File localeFile = new File(folder, localeString + ".yml");
			if (!localeFile.exists()) {
				plugin.saveResource(FOLDERNAME + File.separatorChar + localeString + ".yml", false);
			}
		});

		dataFile = new File(folder, filename);
		if (!dataFile.exists()) {
			dataFile = new File(folder, "en-US.yml");
		}

		config = YamlConfiguration.loadConfiguration(dataFile);
	}

	public String get(String ref, Object... args) {
		if (config.contains(ref)) {
			return MessageFormat.format(MessageUtils.color(config.getString(ref)), args);
		}
		return ref;
	}

	public List<String> getList(String ref, Object... args) {
		if (config.contains(ref)) {
			return config.getStringList(ref).stream()
					.map(string -> MessageFormat.format(MessageUtils.color(string), args)).collect(Collectors.toList());
		}
		return Lists.newArrayList(ref);
	}
	
	public void reloadData() {
		config = YamlConfiguration.loadConfiguration(dataFile);
	}

}
