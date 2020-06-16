package com.github.jummes.libs.database.factory;

import com.github.jummes.libs.database.Database;
import com.github.jummes.libs.database.MySQLDatabase;
import com.github.jummes.libs.database.YamlDatabase;
import com.github.jummes.libs.model.Model;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DatabaseFactory {

    public static <T extends Model> Database<T> createDatabase(String databaseType, Class<T> modelClass, JavaPlugin plugin) {
        try {
            switch (databaseType) {
                case "mysql":
                    return new MySQLDatabase<>(modelClass, plugin);
                case "yaml":
                default:
                    return new YamlDatabase<>(modelClass, plugin);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Something went bad in database initialization, trying to create a YamlDatabase");
            try {
                return new YamlDatabase<>(modelClass, plugin);
            } catch (Exception exception) {
                Bukkit.getLogger().severe("Couldn't create a YamlDatabase");
                Bukkit.getPluginManager().disablePlugin(plugin);
                return null;
            }
        }
    }

}
