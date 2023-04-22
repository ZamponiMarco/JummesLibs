package com.github.jummes.libs.database;

import com.github.jummes.libs.model.NamedModel;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Database<T extends NamedModel> {

    protected final Class<T> classObject;
    protected JavaPlugin plugin;

    protected final List<String> usedNames = new ArrayList<>();

    public Database(@NonNull Class<T> classObject, @NonNull JavaPlugin plugin, Map<String, Object> args) {
        this.classObject = classObject;
        this.plugin = plugin;
    }

    public abstract void openConnection();

    public abstract void closeConnection();

    public abstract T loadObject(String name);

    public abstract List<T> loadObjects();

    public abstract void saveObject(@NonNull T object);

    public abstract void deleteObject(@NonNull T object);

    protected void validateDeserializedName(@NonNull T t) {
        String newName;
        while (usedNames.contains(t.getName())) {
            newName = t.getName() + "-copy";
            t.setName(newName);
            t.setOldName(newName);
        }
    }

}
