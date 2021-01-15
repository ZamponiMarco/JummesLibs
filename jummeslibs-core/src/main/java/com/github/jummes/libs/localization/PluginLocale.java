package com.github.jummes.libs.localization;

import com.github.jummes.libs.util.MessageUtils;
import com.google.common.collect.Lists;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PluginLocale {

    private final static String FOLDERNAME = "locale";
    private Map<Plugin, List<String>> defaultLocales;
    private List<File> localeFiles;
    private YamlConfiguration config;

    public PluginLocale() {
        this.defaultLocales = new HashMap<>();
        this.localeFiles = new ArrayList<>();
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

    private void registerDefaultLocales(Plugin plugin, List<String> locales) {
        this.defaultLocales.put(plugin, locales);
        defaultLocales.get(plugin).forEach(locale -> plugin.saveResource(FOLDERNAME + File.separatorChar +
                locale + ".yml", true));
    }

    private void loadData() {
        config = localeFiles.stream().map(YamlConfiguration::loadConfiguration).reduce((config1, config2) -> {
            config2.getKeys(true).forEach(key -> {
                        if (!config2.isConfigurationSection(key))
                            config1.set(key, config2.get(key));
                    }
            );
            return config1;
        }).orElse(new YamlConfiguration());
    }

    public void registerLocaleFiles(Plugin plugin, List<String> defaultLocales, String filename) {
        registerDefaultLocales(plugin, defaultLocales);

        File dataFile = getDataFile(filename, plugin);

        if (dataFile.exists()) {
            localeFiles.add(dataFile);
            loadData();
        }
    }

    private File getDataFile(String filename, Plugin plugin) {
        File folder = new File(plugin.getDataFolder(), FOLDERNAME);

        if (!folder.exists()) {
            folder.mkdir();
        }

        File dataFile = new File(folder, filename + ".yml");
        if (!dataFile.exists()) {
            dataFile = new File(folder, "en-US.yml");
        }
        return dataFile;
    }

    private void reloadData(String fileName, Plugin plugin) {
        File file = getDataFile(fileName, plugin);
        this.localeFiles.remove(file);
        registerLocaleFiles(plugin, defaultLocales.get(plugin), fileName);
    }


}
