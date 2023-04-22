package com.github.jummes.libs.model;

import com.github.jummes.libs.database.Database;
import com.github.jummes.libs.database.factory.DatabaseFactory;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * It's a class that manages data in memory about a certain Model object, it
 * only provides a method to update the model in the database, it probably has
 * to be built better
 *
 * @param <T> the model the manager will handle
 * @author Marco
 */
public abstract class ModelManager<T extends NamedModel> {

    private final Database<T> database;

    public ModelManager(Class<T> classObject, String databaseType, JavaPlugin plugin, Map<String, Object> args) {
        this.database = DatabaseFactory.createDatabase(databaseType, classObject, plugin, args);
    }

    public List<T> fetchModels() { return database.loadObjects(); }

    public T fetchModel(@NonNull String name) {
        return database.loadObject(name);
    }

    public void saveModel(@NonNull T object) {
        database.saveObject(object);
    }

    public void deleteModel(@NonNull T object) {
        database.deleteObject(object);
    }

}
