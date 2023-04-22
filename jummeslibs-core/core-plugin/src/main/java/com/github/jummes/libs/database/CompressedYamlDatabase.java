package com.github.jummes.libs.database;

import com.github.jummes.libs.model.NamedModel;
import com.github.jummes.libs.util.CompressUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class CompressedYamlDatabase<T extends NamedModel> extends YamlDatabase<T> {

    public CompressedYamlDatabase(@NonNull Class<T> classObject, @NonNull JavaPlugin plugin, Map<String, Object> args) {
        super(classObject, plugin, args);
    }

    @Override
    public T loadObject(String name) {
        loadConfiguration();

        try {
            String string = yamlConfiguration.getString(name);
            T obj = (T) NamedModel.fromSerializedString(new String(CompressUtils.
                    decompress(Base64.getDecoder().decode(string)), Charset.defaultCharset()));
            return obj;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SneakyThrows
    @Override
    public List<T> loadObjects() {
        loadConfiguration();

        List<T> list = new ArrayList<>();

        try {
            yamlConfiguration.getKeys(false).forEach(key -> {
                        String string = yamlConfiguration.getString(key);
                        T obj = (T) NamedModel.fromSerializedString(new String(CompressUtils.
                                decompress(Base64.getDecoder().decode(string)), Charset.defaultCharset()));
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
    private void loadConfiguration() {
        this.yamlConfiguration.load(dataFile);
    }
}
