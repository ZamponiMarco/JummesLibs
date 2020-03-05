package com.github.jummes.libs.gui.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.FieldInventoryHolderFactory;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.setting.change.CollectionAddInformation;
import com.github.jummes.libs.gui.setting.change.CollectionRemoveInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class ObjectCollectionInventoryHolder extends ModelObjectInventoryHolder {

	private static final String ADD_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZjNmRjZjczOWMxMWZlY2U0M2NkZDE4NGRlYTc5MWNmNzU3YmY3YmQ5MTUzNmZkYmM5NmZhNDdhY2ZiIn19fQ==";
	private static final String NEXT_PAGE_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTdiMDNiNzFkM2Y4NjIyMGVmMTIyZjk4MzFhNzI2ZWIyYjI4MzMxOWM3YjYyZTdkY2QyZDY0ZDk2ODIifX19==";
	private static final String PREVIOUS_PAGE_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDgzNDhhYTc3ZjlmYjJiOTFlZWY2NjJiNWM4MWI1Y2EzMzVkZGVlMWI5MDVmM2E4YjkyMDk1ZDBhMWYxNDEifX19==";

	private final static int OBJECTS_NUMBER = 50;

	protected int page;
	protected Field field;

	public ObjectCollectionInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field, int page) {
		super(plugin, parent, path);
		this.field = field;
		this.page = page;
	}

	@Override
	protected void initializeInventory() {
		try {
			List<Object> objects = Lists.newArrayList((Collection<Object>) FieldUtils.readField(field,
					path.getLast() != null ? path.getLast() : path.getModelManager(), true));
			List<Object> toList = objects.subList((page - 1) * OBJECTS_NUMBER,
					Math.min(objects.size(), page * OBJECTS_NUMBER));
			int maxPage = (int) Math.ceil((objects.size() > 0 ? objects.size() : 1) / (double) OBJECTS_NUMBER);
			this.inventory = Bukkit.createInventory(this, 54, MessageUtils.color("&c&l" + field.getName()));
			IntStream.range(0, toList.size()).forEach(i -> {
				registerClickConsumer(i, ItemUtils.getNamedItem(new ItemStack(Material.PAPER), toList.get(i).toString(),
						new ArrayList<String>()), e -> {
							if (e.getClick().equals(ClickType.LEFT)) {
								e.getWhoClicked().openInventory(
										FieldInventoryHolderFactory.createFieldInCollectionInventoryHolder(plugin, this,
												path, field, toList.get(i)).getInventory());
							} else if (e.getClick().equals(ClickType.RIGHT)) {
								new CollectionRemoveInformation(field, toList.get(i)).setValue(path, null);
								e.getWhoClicked().openInventory(getInventory());
							}
						});
			});

			registerClickConsumer(50, getAddItem(), e -> {
				Class<?> containedClass = TypeToken.of(field.getGenericType())
						.resolveType(field.getType().getTypeParameters()[0]).getRawType();
				try {
					new CollectionAddInformation(field).setValue(path,
							ClassUtils.primitiveToWrapper(containedClass).newInstance());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				e.getWhoClicked().openInventory(getInventory());
			});

			if (page != maxPage) {
				registerClickConsumer(52,
						ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(NEXT_PAGE_ITEM),
								MessageUtils.color("&6&lNext page"), new ArrayList<String>()),
						e -> e.getWhoClicked().openInventory(
								new ObjectCollectionInventoryHolder(plugin, parent, path, field, page + 1)
										.getInventory()));
			}
			if (page != 1) {
				registerClickConsumer(51,
						ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(PREVIOUS_PAGE_ITEM),
								MessageUtils.color("&6&lPrevious page"), new ArrayList<String>()),
						e -> e.getWhoClicked().openInventory(
								new ObjectCollectionInventoryHolder(plugin, parent, path, field, page - 1)
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