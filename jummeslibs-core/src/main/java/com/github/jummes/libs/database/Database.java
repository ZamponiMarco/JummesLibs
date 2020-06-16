package com.github.jummes.libs.database;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.model.Model;

import lombok.NonNull;

public abstract class Database<T extends Model> {

    protected final Class<T> classObject;
    protected JavaPlugin plugin;

    public Database(@NonNull Class<T> classObject, @NonNull JavaPlugin plugin) {
        this.classObject = classObject;
        this.plugin = plugin;
    }

    protected abstract void openConnection() throws Exception;

    public abstract void closeConnection();

    public abstract void loadObjects(List<T> list);

    public abstract void saveObject(@NonNull T object);

    public abstract void deleteObject(@NonNull T object);

}
