package com.github.jummes.libs.model;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.database.Database;
import com.github.jummes.libs.database.factory.DatabaseFactory;

import lombok.NonNull;

/**
 * It's a class that manages data in memory about a certain Model object, it
 * only provides a method to update the model in the database, it probably has
 * to be built better
 *
 * @param <T> the model the manager will handle
 * @author Marco
 */
public abstract class ModelManager<T extends Model> {

    protected Database<T> database;

    public ModelManager(Class<T> classObject, String databaseType, JavaPlugin plugin) {
        this.database = DatabaseFactory.createDatabase(databaseType, classObject, plugin);
    }

    public void saveModel(@NonNull T object) {
        database.saveObject(object);
    }

    public void deleteModel(@NonNull T object) {
        database.deleteObject(object);
    }

}
