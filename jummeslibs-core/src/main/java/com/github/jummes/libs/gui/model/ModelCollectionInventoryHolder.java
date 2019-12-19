package com.github.jummes.libs.gui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.google.common.collect.Lists;

public class ModelCollectionInventoryHolder<T extends Model> extends ModelObjectInventoryHolder<Model> {

	private static final int MODELS_NUMBER = 50;

	protected List<T> models;
	protected int page;

	public ModelCollectionInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Collection<T> models, int page) {
		super(plugin, parent, path);
		this.models = new ArrayList<T>(models);
		this.page = page;
	}

	@Override
	protected void initializeInventory() {
		List<Model> models = Lists.newArrayList(this.models);
		List<Model> toList = models.stream().filter(model -> models.indexOf(model) >= (page - 1) * MODELS_NUMBER
				&& models.indexOf(model) <= page * MODELS_NUMBER - 1).collect(Collectors.toList());
		int maxPage = (int) Math.ceil((models.size() > 0 ? models.size() : 1) / (double) MODELS_NUMBER);

		this.inventory = Bukkit.createInventory(this, 54, path.getLast().getClass().getSimpleName());
		
		toList.forEach(model -> {
			registerClickConsumer(toList.indexOf(model), model.getGUIItem(), e -> {
				e.getWhoClicked().openInventory(new ModelObjectInventoryHolder<>(plugin, parent, path).getInventory());
			});
		});
		
		if (page != maxPage) {
			registerClickConsumer(53, new ItemStack(Material.ACACIA_BOAT), e -> e.getWhoClicked().openInventory(
					new ModelCollectionInventoryHolder<>(plugin, parent, path, models, page + 1).getInventory()));
		}
		if (page != 1) {
			registerClickConsumer(52, new ItemStack(Material.ACACIA_BUTTON), e -> e.getWhoClicked().openInventory(
					new ModelCollectionInventoryHolder<>(plugin, parent, path, models, page - 1).getInventory()));
		}
		fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
	}
}
