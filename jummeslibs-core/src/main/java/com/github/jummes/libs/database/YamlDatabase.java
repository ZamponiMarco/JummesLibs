package com.github.jummes.libs.database;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.math.IntRange;
import com.github.jummes.libs.model.wrapper.ItemMetaWrapper;
import com.github.jummes.libs.model.wrapper.ItemStackWrapper;
import com.github.jummes.libs.model.wrapper.LocationWrapper;

import lombok.NonNull;

public class YamlDatabase<T extends Model> extends Database<T> {

    private static final String FILE_SUFFIX = ".yml";

    private String name;
    private File dataFile;
    private YamlConfiguration yamlConfiguration;

    public YamlDatabase(Class<T> classObject, JavaPlugin plugin) {
        super(classObject, plugin);
    }

    @Override
    protected void openConnection() {
        this.name = classObject.getSimpleName().toLowerCase();

        this.dataFile = new File(plugin.getDataFolder(), name.concat(FILE_SUFFIX));

        if (!this.dataFile.exists()) {
            plugin.saveResource(classObject.getSimpleName().toLowerCase().concat(FILE_SUFFIX), false);
        }

        this.yamlConfiguration = new YamlConfiguration();
        try {
            this.yamlConfiguration.load(dataFile);
        } catch (InvalidConfigurationException e) {
            try {
                Bukkit.getLogger().warning("Error in " + name + FILE_SUFFIX + ". Trying to fix it.");
                Charset charset = StandardCharsets.UTF_8;
                String content = new String(Files.readAllBytes(dataFile.toPath()), charset);
                content = content.replaceAll("==: IntRange", "==: " + IntRange.class.getName());
                content = content.replaceAll("==: com.github.jummes.libs.model.math.IntRange",
                        "==: " + IntRange.class.getName());
                content = content.replaceAll("==: LocationWrapper", "==: " + LocationWrapper.class.getName());
                content = content.replaceAll("==: com.github.jummes.libs.model.wrapper.LocationWrapper",
                        "==: " + LocationWrapper.class.getName());
                content = content.replaceAll("==: ItemStackWrapper", "==: " + ItemStackWrapper.class.getName());
                content = content.replaceAll("==: com.github.jummes.libs.model.wrapper.ItemStackWrapper",
                        "==: " + ItemStackWrapper.class.getName());
                content = content.replaceAll("==: ItemMetaWrapper", "==: " + ItemMetaWrapper.class.getName());
                content = content.replaceAll("==: com.github.jummes.libs.model.wrapper.ItemMetaWrapper",
                        "==: " + ItemMetaWrapper.class.getName());
                Files.write(dataFile.toPath(), content.getBytes(charset));
                e.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void closeConnection() {
        try {
            yamlConfiguration.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<T> loadObjects() {
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

}
