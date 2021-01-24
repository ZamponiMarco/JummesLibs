package com.github.jummes.libs.database;

import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.model.Model;

import lombok.NonNull;

public abstract class Database<T extends Model> {

    protected final Class<T> classObject;
    protected JavaPlugin plugin;

    public Database(@NonNull Class<T> classObject, @NonNull JavaPlugin plugin, Map<String, Object> args) {
        this.classObject = classObject;
        this.plugin = plugin;
    }

    public abstract void openConnection();

    public abstract void closeConnection();

    public abstract List<T> loadObjects();

    public abstract void saveObject(@NonNull T object);

    public abstract void deleteObject(@NonNull T object);

}
