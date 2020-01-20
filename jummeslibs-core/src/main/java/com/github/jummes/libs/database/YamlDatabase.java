package com.github.jummes.libs.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.model.Model;

import lombok.NonNull;

public class YamlDatabase<T extends Model> extends Database<T> {

	private static final String FILE_SUFFIX = ".yml";

	private String name;
	private File dataFile;
	private YamlConfiguration yamlConfiguration;

	public YamlDatabase(Class<T> classObject, JavaPlugin plugin) {
		super(classObject, plugin);
	}

	@Override
	protected void openConnection() {
		this.name = classObject.getSimpleName().toLowerCase();

		this.dataFile = new File(plugin.getDataFolder(), name.concat(FILE_SUFFIX));

		if (!this.dataFile.exists()) {
			plugin.saveResource(classObject.getSimpleName().toLowerCase().concat(FILE_SUFFIX), false);
		}

		this.yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);
	}

	@Override
	protected void closeConnection() {
		try {
			yamlConfiguration.save(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<T> loadObjects() {
		return yamlConfiguration.getObject(name, List.class, new ArrayList<>());
	}

	@Override
	public void saveObject(@NonNull T object) {
		List<T> list = yamlConfiguration.getObject(name, List.class, new ArrayList<>());
		list.remove(object);
		list.add(object);
		yamlConfiguration.set(name, list);
		try {
			yamlConfiguration.save(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteObject(@NonNull T object) {
		List<T> list = yamlConfiguration.getObject(name, List.class, new ArrayList<>());
		list.remove(object);
		yamlConfiguration.set(name, list);
		try {
			yamlConfiguration.save(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
