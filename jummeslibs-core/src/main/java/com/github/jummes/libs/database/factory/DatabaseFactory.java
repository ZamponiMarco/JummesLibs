package com.github.jummes.libs.database.factory;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.database.Database;
import com.github.jummes.libs.database.YamlDatabase;
import com.github.jummes.libs.model.Model;

public class DatabaseFactory {

    public static <T extends Model> Database<T> createDatabase(String databaseType, Class<T> modelClass, JavaPlugin plugin) {
        switch (databaseType) {
            case "yaml":
            default:
                return new YamlDatabase<T>(modelClass, plugin);
        }
    }

}
