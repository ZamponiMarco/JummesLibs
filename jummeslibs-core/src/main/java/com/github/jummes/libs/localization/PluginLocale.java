package com.github.jummes.libs.localization;

import com.github.jummes.libs.util.MessageUtils;
import com.google.common.collect.Lists;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public class PluginLocale {

    private final static String FOLDERNAME = "locale";
    private final JavaPlugin plugin;
    private final List<String> defaultLocales;
    private File dataFile;
    private YamlConfiguration config;

    public PluginLocale(JavaPlugin plugin, List<String> defaultLocales, String filename) {
        this.plugin = plugin;
        this.defaultLocales = defaultLocales;
        loadData(filename);
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

    private void loadData(String filename) {
        setDataFile(filename);
        config = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void setDataFile(String filename) {
        File folder = new File(plugin.getDataFolder(), FOLDERNAME);

        if (!folder.exists()) {
            folder.mkdir();
        }

        defaultLocales.forEach(localeString -> {
            plugin.saveResource(FOLDERNAME + File.separatorChar + localeString + ".yml", true);
        });

        dataFile = new File(folder, filename + ".yml");
        if (!dataFile.exists()) {
            dataFile = new File(folder, "en-US.yml");
        }
    }

    public void reloadData(String filename) {
        loadData(filename);
    }

}
