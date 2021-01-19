package com.github.jummes.libs.gui.setting;

import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.setting.change.ChangeInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class NumberFieldChangeInventoryHolder<S extends Number> extends FieldChangeInventoryHolder {

    protected static final String ARROW_LEFT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2NzExOTgzODJkZTkzZTFkM2M3ODM0ZGU4NjcwNGE2ZWNjNzkxNDE5ZjBkZGI0OWE0MWE5NjA4YWQ0NzIifX19";
    protected static final String ARROW2_LEFT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDgzNDhhYTc3ZjlmYjJiOTFlZWY2NjJiNWM4MWI1Y2EzMzVkZGVlMWI5MDVmM2E4YjkyMDk1ZDBhMWYxNDEifX19";
    protected static final String ARROW3_LEFT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzMDFhMTdjOTU1ODA3ZDg5ZjljNzJhMTkyMDdkMTM5M2I4YzU4YzRlNmU0MjBmNzE0ZjY5NmE4N2ZkZCJ9fX0";
    protected static final String ARROW_RIGHT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODEzM2E0MjM2MDY2OTRkYTZjOTFhODRlYTY2ZDQ5ZWZjM2EyM2Y3M2ZhOGFmOGNjMWZlMjk4M2ZlOGJiNWQzIn19fQ";
    protected static final String ARROW2_RIGHT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTdiMDNiNzFkM2Y4NjIyMGVmMTIyZjk4MzFhNzI2ZWIyYjI4MzMxOWM3YjYyZTdkY2QyZDY0ZDk2ODIifX19";
    protected static final String ARROW3_RIGHT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU0ZmFiYjE2NjRiOGI0ZDhkYjI4ODk0NzZjNmZlZGRiYjQ1MDVlYmE0Mjg3OGM2NTNhNWQ3OTNmNzE5YjE2In19fQ";
    protected static final String ZERO_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWY4ODZkOWM0MGVmN2Y1MGMyMzg4MjQ3OTJjNDFmYmZiNTRmNjY1ZjE1OWJmMWJjYjBiMjdiM2VhZDM3M2IifX19";
    protected static final String SUBMIT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdiNjJkMjc1ZDg3YzA5Y2UxMGFjYmNjZjM0YzRiYTBiNWYxMzVkNjQzZGM1MzdkYTFmMWRmMzU1YTIyNWU4MiJ9fX0";
    protected static final String MINUS_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ2YjEyOTNkYjcyOWQwMTBmNTM0Y2UxMzYxYmJjNTVhZTVhOGM4ZjgzYTE5NDdhZmU3YTg2NzMyZWZjMiJ9fX0=";
    protected static final String PLUS_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZjNmRjZjczOWMxMWZlY2U0M2NkZDE4NGRlYTc5MWNmNzU3YmY3YmQ5MTUzNmZkYmM5NmZhNDdhY2ZiIn19fQ==";
    protected static final String MENU_TITLE = MessageUtils.color("&6&lModify &e&l %s");
    protected static final String MODIFY_SUCCESS = MessageUtils.color("&aObject modified: &6%s: &e%s");
    protected static final String MODIFY_ITEM = MessageUtils.color("&6&lModify -> &e&l%s");
    protected static final String CONFIRM_ITEM = MessageUtils.color("&6&lResult = &e&l%s");
    protected static final String ZERO_ITEM = MessageUtils.color("&6Set to &e&l0");
    protected static final String ZOOM_IN = MessageUtils.color("&6&lDecrease the scale.");
    protected static final String ZOOM_OUT = MessageUtils.color("&6&lIncrease the scale.");

    protected S result;
    protected boolean annotationPresent;
    protected int minValue;
    protected int maxValue;
    protected double scale;

    public NumberFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path, ChangeInformation changeInformation) {
        super(plugin, parent, path, changeInformation);
        result = (S) changeInformation.getValue(path);
        if (changeInformation.getField().isAnnotationPresent(Serializable.Number.class)) {
            annotationPresent = true;
            minValue = changeInformation.getField().getAnnotation(Serializable.Number.class).minValue();
            maxValue = changeInformation.getField().getAnnotation(Serializable.Number.class).maxValue();
            scale = changeInformation.getField().getAnnotation(Serializable.Number.class).scale();
        } else {
            scale = getDefaultScale();
        }
    }

    public NumberFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
                                            ModelPath<? extends Model> path, ChangeInformation changeInformation,
                                            Serializable.Number values) {
        super(plugin, parent, path, changeInformation);
        result = (S) changeInformation.getValue(path);
        annotationPresent = true;
        minValue = values.minValue();
        maxValue = values.maxValue();
        scale = values.scale();
    }


    @Override
    protected void initializeInventory() {

        this.inventory = Bukkit.createInventory(this, 27, String.format(MENU_TITLE, changeInformation.getName()));

        fillModifyButtons();
        registerClickConsumer(22, getZeroItem(), getZeroConsumer());
        registerClickConsumer(26, getBackItem(), getBackConsumer());
        registerClickConsumer(0, ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(MINUS_HEAD), ZOOM_IN, Lists.newArrayList()), e -> {
            scale /= 10;
            fillModifyButtons();
        });
        registerClickConsumer(8, ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(PLUS_HEAD), ZOOM_OUT, Lists.newArrayList()), e -> {
            scale *= 10;
            fillModifyButtons();
        });
        fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
    }

    private void fillModifyButtons() {
        if (!annotationPresent || compareTo(result, minValue) != 0) {
            registerClickConsumer(9, getModifyItem(-scale * 100, wrapper.skullFromValue(ARROW3_LEFT_HEAD)),
                    getModifyConsumer(-scale * 100));
            registerClickConsumer(10, getModifyItem(-scale * 10, wrapper.skullFromValue(ARROW2_LEFT_HEAD)),
                    getModifyConsumer(-scale * 10));
            registerClickConsumer(11, getModifyItem(-scale, wrapper.skullFromValue(ARROW_LEFT_HEAD)),
                    getModifyConsumer(-scale));
        } else {
            registerEmptySlot(9);
            registerEmptySlot(10);
            registerEmptySlot(11);
        }

        registerClickConsumer(13, getConfirmItem(), getConfirmConsumer());

        if (!annotationPresent || result.intValue() != maxValue) {
            registerClickConsumer(15, getModifyItem(+scale, wrapper.skullFromValue(ARROW_RIGHT_HEAD)),
                    getModifyConsumer(+scale));
            registerClickConsumer(16, getModifyItem(+scale * 10, wrapper.skullFromValue(ARROW2_RIGHT_HEAD)),
                    getModifyConsumer(scale * 10));
            registerClickConsumer(17, getModifyItem(+scale * 100, wrapper.skullFromValue(ARROW3_RIGHT_HEAD)),
                    getModifyConsumer(+scale * 100));
        } else {
            registerEmptySlot(15);
            registerEmptySlot(16);
            registerEmptySlot(17);
        }
        fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
    }


    private Consumer<InventoryClickEvent> getModifyConsumer(double addition) {
        return e -> {
            if (e.getClick().equals(ClickType.LEFT)) {
                S operationResult = sum(result, addition);
                checkConstraintsAndAssign(roundNumber(operationResult));
                fillModifyButtons();
            }
        };
    }

    private Consumer<InventoryClickEvent> getZeroConsumer() {
        return e -> {
            if (e.getClick().equals(ClickType.LEFT)) {
                S operationResult = getZero();
                checkConstraintsAndAssign(operationResult);
                fillModifyButtons();
            }
        };
    }

    private void checkConstraintsAndAssign(S operationResult) {
        if (annotationPresent) {
            if (compareTo(operationResult, maxValue) > 0) {
                assignResultValue(maxValue);
            } else if (compareTo(operationResult, minValue) < 0) {
                assignResultValue(minValue);
            } else {
                this.result = operationResult;
            }
        } else {
            this.result = operationResult;
        }
    }

    private Consumer<InventoryClickEvent> getConfirmConsumer() {
        return e -> {
            HumanEntity p = e.getWhoClicked();
            changeInformation.setValue(path, result);
            p.sendMessage(String.format(MODIFY_SUCCESS, changeInformation.getName(), result));
            getBackConsumer().accept(e);
        };
    }

    private ItemStack getModifyItem(double i, ItemStack item) {
        return ItemUtils.getNamedItem(item, String.format(MODIFY_ITEM, i), new ArrayList<>());
    }

    private ItemStack getConfirmItem() {
        return ItemUtils.getNamedItem(wrapper.skullFromValue(SUBMIT_HEAD),
                String.format(CONFIRM_ITEM, result), new ArrayList<>());
    }

    private ItemStack getZeroItem() {
        return ItemUtils.getNamedItem(wrapper.skullFromValue(ZERO_HEAD), ZERO_ITEM, new ArrayList<>());
    }

    protected abstract double getDefaultScale();

    protected abstract void assignResultValue(int value);

    protected abstract int compareTo(S result, int value);

    protected abstract S getZero();

    protected abstract S roundNumber(S operationResult);

    protected abstract S sum(S result, double addition);
}
