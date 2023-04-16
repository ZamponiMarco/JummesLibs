package com.github.jummes.libs.database;

import com.github.jummes.libs.model.Model;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlDatabase<T extends Model> extends Database<T> {

    private static final String FILE_SUFFIX = ".yml";

    private final String name;
    private final File dataFile;
    private YamlConfiguration yamlConfiguration;

    public YamlDatabase(Class<T> classObject, JavaPlugin plugin, Map<String, Object> args) {
        super(classObject, plugin, args);
        this.name = (String) args.getOrDefault("name", classObject.getSimpleName().toLowerCase());
        String fileName = (String) args.getOrDefault("fileName", name.concat(FILE_SUFFIX));
        this.dataFile = new File(plugin.getDataFolder(), fileName);
        if (!this.dataFile.exists()) {
            plugin.saveResource(fileName, false);
        }
    }

    @Override
    public void openConnection() {
        this.yamlConfiguration = new YamlConfiguration();
    }

    @Override
    public void closeConnection() {
        try {
            yamlConfiguration.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<T> loadObjects() {
        loadConfiguration();
        return yamlConfiguration.getObject(name, List.class, new ArrayList<>());
    }

    @Override
    public void saveObject(@NonNull T object) {
        List<T> list = yamlConfiguration.getObject(name, List.class, new ArrayList<>());
        list.remove(object);
        list.add(object);
        yamlConfiguration.set(name, list);
        try {
            yamlConfiguration.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteObject(@NonNull T object) {
        List<T> list = yamlConfiguration.getObject(name, List.class, new ArrayList<>());
        list.remove(object);
        yamlConfiguration.set(name, list);
        try {
            yamlConfiguration.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfiguration() {
        try {
            this.yamlConfiguration.load(dataFile);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error in " + name + FILE_SUFFIX + ".");
        }
    }

}
