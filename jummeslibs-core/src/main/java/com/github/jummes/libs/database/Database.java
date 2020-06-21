package com.github.jummes.libs.database;

import com.github.jummes.libs.model.Model;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class Database<T extends Model> {

    protected static final Object lock = new Object();

    protected final Class<T> classObject;
    protected JavaPlugin plugin;

    public Database(@NonNull Class<T> classObject, @NonNull JavaPlugin plugin) {
        this.classObject = classObject;
        this.plugin = plugin;
    }

    protected abstract void openConnection() throws Exception;

    public abstract void closeConnection();

    public void loadObjects(List<T> list) {
        loadObjects(list, () -> {
        });
    }

    public abstract void loadObjects(List<T> list, Runnable r);

    public abstract void saveObject(@NonNull T object);

    public abstract void deleteObject(@NonNull T object);

}
