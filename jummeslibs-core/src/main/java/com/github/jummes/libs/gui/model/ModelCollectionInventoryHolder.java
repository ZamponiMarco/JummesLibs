package com.github.jummes.libs.gui.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.create.ModelCreateInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelManager;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;
import com.google.common.collect.Lists;

public class ModelCollectionInventoryHolder extends ModelObjectInventoryHolder {

	private static final String ADD_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZjNmRjZjczOWMxMWZlY2U0M2NkZDE4NGRlYTc5MWNmNzU3YmY3YmQ5MTUzNmZkYmM5NmZhNDdhY2ZiIn19fQ==";
	private static final String NEXT_PAGE_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTdiMDNiNzFkM2Y4NjIyMGVmMTIyZjk4MzFhNzI2ZWIyYjI4MzMxOWM3YjYyZTdkY2QyZDY0ZDk2ODIifX19==";
	private static final String PREVIOUS_PAGE_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDgzNDhhYTc3ZjlmYjJiOTFlZWY2NjJiNWM4MWI1Y2EzMzVkZGVlMWI5MDVmM2E4YjkyMDk1ZDBhMWYxNDEifX19==";

	private static final int MODELS_NUMBER = 50;

	protected int page;
	protected Field field;

	public ModelCollectionInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field, int page) {
		super(plugin, parent, path);
		this.field = field;
		this.page = page;
	}
	
	@SuppressWarnings("rawtypes")
	public ModelCollectionInventoryHolder(JavaPlugin plugin, ModelManager<? extends Model> manager, String fieldName) throws NoSuchFieldException, SecurityException {
		this(plugin, null, new ModelPath(manager, null), manager.getClass().getDeclaredField(fieldName), 1);
	}

	@Override
	protected void initializeInventory() {
		/*
		 * Get the list of models to display in the current page
		 */
		try {
			List<Model> models = Lists.newArrayList((Collection<Model>) FieldUtils.readField(field,
					path.getLast() != null ? path.getLast() : path.getModelManager(), true));
			List<Model> toList = models.stream().filter(model -> models.indexOf(model) >= (page - 1) * MODELS_NUMBER
					&& models.indexOf(model) <= page * MODELS_NUMBER - 1).collect(Collectors.toList());
			int maxPage = (int) Math.ceil((models.size() > 0 ? models.size() : 1) / (double) MODELS_NUMBER);

			/*
			 * Create the inventory
			 */
			this.inventory = Bukkit.createInventory(this, 54,
					MessageUtils.color("&6Collection of &c&l" + field.getName()));

			/*
			 * For each model that has to be listed set an item in the GUI
			 */
			toList.forEach(model -> {
				registerClickConsumer(toList.indexOf(model), model.getGUIItem(), e -> {
					try {
						/*
						 * If left clicked open the model GUI inventory
						 */
						if (e.getClick().equals(ClickType.LEFT)) {
							path.addModel(model);
							e.getWhoClicked()
									.openInventory(new ModelObjectInventoryHolder(plugin, this, path).getInventory());
						}

						/*
						 * If right clicked removes the model from the collection
						 */
						else if (e.getClick().equals(ClickType.RIGHT)) {
							((Collection<Model>) FieldUtils.readField(field,
									path.getLast() != null ? path.getLast() : path.getModelManager(), true))
											.remove(model);
							path.addModel(model);
							path.deleteModel();
							path.popModel();
							e.getWhoClicked()
									.openInventory(new ModelCollectionInventoryHolder(plugin, parent, path, field, page)
											.getInventory());
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
			});

			/*
			 * Sets up a model creator inventory, page scrollers, back button and fills the
			 * rest
			 */
			registerClickConsumer(50, getAddItem(), e -> {
				e.getWhoClicked()
						.openInventory(new ModelCreateInventoryHolder(plugin, this, path, field).getInventory());
			});
			if (page != maxPage) {
				registerClickConsumer(52,
						ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(NEXT_PAGE_ITEM),
								MessageUtils.color("&6&lNext page"), new ArrayList<String>()),
						e -> e.getWhoClicked()
								.openInventory(new ModelCollectionInventoryHolder(plugin, parent, path, field, page + 1)
										.getInventory()));
			}
			if (page != 1) {
				registerClickConsumer(51,
						ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(PREVIOUS_PAGE_ITEM),
								MessageUtils.color("&6&lPrevious page"), new ArrayList<String>()),
						e -> e.getWhoClicked()
								.openInventory(new ModelCollectionInventoryHolder(plugin, parent, path, field, page - 1)
										.getInventory()));
			}
			registerClickConsumer(53, getBackItem(), getBackConsumer());
			fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ItemStack getAddItem() {
		return ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(ADD_ITEM), MessageUtils.color("&6&lAdd"),
				new ArrayList<String>());
	}
}
