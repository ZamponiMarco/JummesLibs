package com.github.jummes.libs.database.factory;

import com.github.jummes.libs.database.CompressedYamlDatabase;
import com.github.jummes.libs.database.Database;
import com.github.jummes.libs.model.NamedModel;
import com.github.jummes.libs.database.YamlDatabase;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class DatabaseFactory {

    @SneakyThrows
    public static <T extends NamedModel> Database<T> createDatabase(String databaseType, Class<T> modelClass,
                                                                    JavaPlugin plugin, Map<String, Object> args) {
        Database<T> database = switch (databaseType) {
            case "yaml":
                yield new YamlDatabase<>(modelClass, plugin, args);
            case "comp_yaml":
                yield new CompressedYamlDatabase<>(modelClass, plugin, args);
            default:
                throw new IllegalStateException("Unexpected value: " + databaseType);
        };
        database.openConnection();
        return database;
    }

}
