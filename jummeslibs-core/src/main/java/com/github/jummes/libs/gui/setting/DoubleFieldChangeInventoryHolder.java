package com.github.jummes.libs.gui.setting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.model.wrapper.ModelWrapper;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;

public class DoubleFieldChangeInventoryHolder extends FieldChangeInventoryHolder {

	private static final String ARROW_LEFT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODEzM2E0MjM2MDY2OTRkYTZjOTFhODRlYTY2ZDQ5ZWZjM2EyM2Y3M2ZhOGFmOGNjMWZlMjk4M2ZlOGJiNWQzIn19fQ";
	private static final String ARROW2_LEFT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDgzNDhhYTc3ZjlmYjJiOTFlZWY2NjJiNWM4MWI1Y2EzMzVkZGVlMWI5MDVmM2E4YjkyMDk1ZDBhMWYxNDEifX19";
	private static final String ARROW3_LEFT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzMDFhMTdjOTU1ODA3ZDg5ZjljNzJhMTkyMDdkMTM5M2I4YzU4YzRlNmU0MjBmNzE0ZjY5NmE4N2ZkZCJ9fX0";
	private static final String ARROW_RIGHT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODEzM2E0MjM2MDY2OTRkYTZjOTFhODRlYTY2ZDQ5ZWZjM2EyM2Y3M2ZhOGFmOGNjMWZlMjk4M2ZlOGJiNWQzIn19fQ";
	private static final String ARROW2_RIGHT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTdiMDNiNzFkM2Y4NjIyMGVmMTIyZjk4MzFhNzI2ZWIyYjI4MzMxOWM3YjYyZTdkY2QyZDY0ZDk2ODIifX19";
	private static final String ARROW3_RIGHT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU0ZmFiYjE2NjRiOGI0ZDhkYjI4ODk0NzZjNmZlZGRiYjQ1MDVlYmE0Mjg3OGM2NTNhNWQ3OTNmNzE5YjE2In19fQ";
	private static final String ZERO_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWY4ODZkOWM0MGVmN2Y1MGMyMzg4MjQ3OTJjNDFmYmZiNTRmNjY1ZjE1OWJmMWJjYjBiMjdiM2VhZDM3M2IifX19";
	private static final String SUBMIT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdiNjJkMjc1ZDg3YzA5Y2UxMGFjYmNjZjM0YzRiYTBiNWYxMzVkNjQzZGM1MzdkYTFmMWRmMzU1YTIyNWU4MiJ9fX0";

	private static final String MENU_TITLE = MessageUtils.color("&6&lModify &e&l %s");
	private static final String MODIFY_SUCCESS = MessageUtils.color("&aObject modified: &6%s: &e%s");
	private static final String MODIFY_ITEM = MessageUtils.color("&6&lModify -> &e&l%s");
	private static final String CONFIRM_ITEM = MessageUtils.color("&6&lResult = &e&l%s");
	private static final String ZERO_ITEM = MessageUtils.color("&6Set to &e&l0");

	private double result;

	public DoubleFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path, Field field) {
		super(plugin, parent, path, field);
		try {
			result = (double) FieldUtils.readField(field, path.getLast(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initializeInventory() {

		this.inventory = Bukkit.createInventory(this, 27, String.format(MENU_TITLE, field.getName()));

		registerClickConsumer(9, getModifyItem(-1, wrapper.skullFromValue(ARROW3_LEFT_HEAD)), getModifyConsumer(-1));
		registerClickConsumer(10, getModifyItem(-0.1, wrapper.skullFromValue(ARROW2_LEFT_HEAD)),
				getModifyConsumer(-0.1));
		registerClickConsumer(11, getModifyItem(-0.01, wrapper.skullFromValue(ARROW_LEFT_HEAD)),
				getModifyConsumer(-0.01));
		registerClickConsumer(13, getConfirmItem(), getConfirmConsumer());
		registerClickConsumer(15, getModifyItem(+0.01, wrapper.skullFromValue(ARROW_RIGHT_HEAD)),
				getModifyConsumer(+0.01));
		registerClickConsumer(16, getModifyItem(+0.1, wrapper.skullFromValue(ARROW2_RIGHT_HEAD)),
				getModifyConsumer(+0.1));
		registerClickConsumer(17, getModifyItem(+1, wrapper.skullFromValue(ARROW3_RIGHT_HEAD)), getModifyConsumer(+1));
		// TODO CORREST BUG WITH ZERO ITEM
		registerClickConsumer(22, getZeroItem(), getModifyConsumer(-result));
		registerClickConsumer(26, getBackItem(), getBackConsumer());
		fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);

	}

	private Consumer<InventoryClickEvent> getModifyConsumer(double addition) {
		return e -> {
			if (e.getClick().equals(ClickType.LEFT)) {
				result += addition;
				result = Math.round(result * 100d) / 100d;
				inventory.setItem(13, getConfirmItem());
			}
		};
	}

	private Consumer<InventoryClickEvent> getConfirmConsumer() {
		return e -> {
			HumanEntity p = e.getWhoClicked();
			try {
				FieldUtils.writeField(field, path.getLast(), result, true);
				if (path.getLast() instanceof ModelWrapper<?>) {
					((ModelWrapper<?>) path.getLast()).changed();
					((ModelWrapper<?>) path.getLast()).notifyObservers(field);
				}
				path.saveModel();
				p.sendMessage(String.format(MODIFY_SUCCESS, field.getName(), String.valueOf(result)));
				getBackConsumer().accept(e);
			} catch (Exception ex) {
				p.closeInventory();
			}
		};
	}

	private ItemStack getModifyItem(double i, ItemStack item) {
		return ItemUtils.getNamedItem(item, String.format(MODIFY_ITEM, String.valueOf(i)), new ArrayList<String>());
	}

	private ItemStack getConfirmItem() {
		return ItemUtils.getNamedItem(wrapper.skullFromValue(SUBMIT_HEAD),
				String.format(CONFIRM_ITEM, String.valueOf(result)), new ArrayList<String>());
	}

	private ItemStack getZeroItem() {
		return ItemUtils.getNamedItem(wrapper.skullFromValue(ZERO_HEAD), ZERO_ITEM, new ArrayList<String>());
	}

}
