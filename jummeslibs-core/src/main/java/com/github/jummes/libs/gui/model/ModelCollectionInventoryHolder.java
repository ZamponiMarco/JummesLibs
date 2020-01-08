package com.github.jummes.libs.gui.model;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.create.ModelCreateInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.google.common.collect.Lists;

public class ModelCollectionInventoryHolder<T extends Model> extends ModelObjectInventoryHolder<Model> {

	private static final int MODELS_NUMBER = 50;

	protected int page;
	protected Field field;

	public ModelCollectionInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field, int page) {
		super(plugin, parent, path);
		this.field = field;
		this.page = page;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initializeInventory() {
		try {
			field.setAccessible(true);
			List<Model> models = Lists.newArrayList(
					(Collection<Model>) field.get(path.getLast() != null ? path.getLast() : path.getModelManager()));
			List<Model> toList = models.stream().filter(model -> models.indexOf(model) >= (page - 1) * MODELS_NUMBER
					&& models.indexOf(model) <= page * MODELS_NUMBER - 1).collect(Collectors.toList());
			int maxPage = (int) Math.ceil((models.size() > 0 ? models.size() : 1) / (double) MODELS_NUMBER);

			this.inventory = Bukkit.createInventory(this, 54, "Collection of " + models.hashCode());

			toList.forEach(model -> {
				registerClickConsumer(toList.indexOf(model), model.getGUIItem(), e -> {
					try {
						field.setAccessible(true);
						if (e.getClick().equals(ClickType.LEFT)) {
							path.addModel(model);
							e.getWhoClicked()
									.openInventory(new ModelObjectInventoryHolder<>(plugin, this, path).getInventory());
						} else if (e.getClick().equals(ClickType.RIGHT)) {
							((Collection<Model>) field.get(path.getLast() != null ? path.getLast() : path.getModelManager()))
									.remove(model);
							path.addModel(model);
							path.deleteModel();
							path.removeModel();
							e.getWhoClicked().openInventory(
									new ModelCollectionInventoryHolder<>(plugin, parent, path, field, page)
											.getInventory());
						}
						field.setAccessible(false);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
			});

			registerClickConsumer(51, getBackItem(), e -> {
				e.getWhoClicked()
						.openInventory(new ModelCreateInventoryHolder(plugin, parent, path, field).getInventory());
			});
			if (page != maxPage) {
				registerClickConsumer(53, new ItemStack(Material.ACACIA_BOAT), e -> e.getWhoClicked().openInventory(
						new ModelCollectionInventoryHolder<>(plugin, parent, path, field, page + 1).getInventory()));
			}
			if (page != 1) {
				registerClickConsumer(52, new ItemStack(Material.ACACIA_BUTTON), e -> e.getWhoClicked().openInventory(
						new ModelCollectionInventoryHolder<>(plugin, parent, path, field, page - 1).getInventory()));
			}
			registerClickConsumer(53, getBackItem(), getBackConsumer());
			fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
			field.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Consumer<InventoryClickEvent> getBackConsumer() {
		return e -> {
			if (parent != null) {
				e.getWhoClicked().openInventory(parent.getInventory());
			} else {
				e.getWhoClicked().closeInventory();
			}
		};
	}
}
