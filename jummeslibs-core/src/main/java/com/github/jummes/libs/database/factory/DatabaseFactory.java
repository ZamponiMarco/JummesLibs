package com.github.jummes.libs.database.factory;

import com.github.jummes.libs.database.Database;
import com.github.jummes.libs.database.YamlDatabase;
import com.github.jummes.libs.model.Model;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class DatabaseFactory {

    @Getter
    private static Map<String, Class<? extends Database>> map = new HashMap<>();

    static {
        map.put("yaml", YamlDatabase.class);
    }

    @SneakyThrows
    public static <T extends Model> Database<T> createDatabase(String databaseType, Class<T> modelClass,
                                                               JavaPlugin plugin, Map<String, Object> args) {
        Database<T> database = map.getOrDefault(databaseType, YamlDatabase.class).getConstructor(Class.class,
                JavaPlugin.class, Map.class).newInstance(modelClass, plugin, args);
        database.openConnection();
        return database;
    }

}
