package com.github.jummes.libs.model;

import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.util.MessageUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.Field;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class NamedModel implements Model {

    private static final String NAME_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTdlZDY2ZjVhNzAyMDlkODIxMTY3ZDE1NmZkYmMwY2EzYmYxMWFkNTRlZDVkODZlNzVjMjY1ZjdlNTAyOWVjMSJ9fX0=";
    @Serializable(headTexture = NAME_HEAD, description = "gui.name")
    @EqualsAndHashCode.Include
    protected String name;
    private String oldName;

    public NamedModel(String name) {
        this.name = name;
        this.oldName = name;
    }

    public NamedModel(Map<String, Object> map) {
        this((String) map.getOrDefault("name", MessageUtils.getRandomString(6)));
    }

    public static NamedModel fromSerializedString(String string) {
        YamlConfiguration config = new YamlConfiguration();
        NamedModel toReturn;
        try {
            config.loadFromString(string);
            toReturn = (NamedModel) config.get("model");
        } catch (ClassCastException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        return toReturn;
    }

    public String toSerializedString() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("model", this);
        return config.saveToString();
    }

    protected abstract boolean isAlreadyPresent(String name);

    @Override
    public Object beforeModify(Field field, Object value) {
        if ("name".equals(field.getName())) {
            String stringValue = (String) value;
            if (isAlreadyPresent(stringValue)) {
                return stringValue.concat("-copy");
            }
        }
        return null;
    }

}
