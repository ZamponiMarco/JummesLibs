package com.github.jummes.libs.database;

import com.github.jummes.libs.model.NamedModel;
import com.github.jummes.libs.util.CompressUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class YamlDatabase<T extends NamedModel> extends Database<T> {

    protected static final String FILE_SUFFIX = ".yml";

    protected final File dataFile;
    protected YamlConfiguration yamlConfiguration;

    public YamlDatabase(Class<T> classObject, JavaPlugin plugin, Map<String, Object> args) {
        super(classObject, plugin, args);
        String name = (String) args.getOrDefault("name", classObject.getSimpleName().toLowerCase());
        String fileName = (String) args.getOrDefault("fileName", name.concat(FILE_SUFFIX));

        // Horrible way to allow addons to provide their own behavior for this
        Supplier<File> fileSupplier = (Supplier<File>) args.get("fileSupplier");

        if (fileSupplier == null) {
            this.dataFile = new File(plugin.getDataFolder(), fileName);
            if (!this.dataFile.exists()) {
                plugin.saveResource(fileName, false);
            }
        } else {
            this.dataFile = fileSupplier.get();
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
    public T loadObject(String name) {
        loadConfiguration();

        try {
            return (T) yamlConfiguration.get(name);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<T> loadObjects() {
        loadConfiguration();

        List<T> list = new ArrayList<>();

        try {
            yamlConfiguration.getKeys(false).forEach(key -> {
                        T obj = (T) yamlConfiguration.get(key);
                        if (obj != null) {
                            list.add(obj);
                            saveObject(obj);
                        }
                    }
            );
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return list;
    }
    @SneakyThrows
    @Override
    public void saveObject(@NonNull T object) {
        if (!object.getName().equals(object.getOldName())) {
            yamlConfiguration.set(object.getOldName(), null);
            usedNames.remove(object.getOldName());
        }
        yamlConfiguration.set(object.getName(), new String(Base64.getEncoder().encode(CompressUtils.
                compress(object.toSerializedString().getBytes())), Charset.defaultCharset()));
        yamlConfiguration.save(dataFile);

        usedNames.add(object.getName());
        object.setOldName(object.getName());
    }

    @SneakyThrows
    @Override
    public void deleteObject(@NonNull T object) {
        usedNames.remove(object.getName());
        yamlConfiguration.set(object.getName(), null);
        yamlConfiguration.save(dataFile);
    }

    @SneakyThrows
    private void loadConfiguration() {
        this.yamlConfiguration.load(dataFile);
    }

}
