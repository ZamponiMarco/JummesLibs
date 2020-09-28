package com.github.jummes.libs.gui.setting;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.setting.change.ChangeInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;

public class BooleanFieldChangeInventoryHolder extends FieldChangeInventoryHolder {

    private static final String TRUE_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdiNjJkMjc1ZDg3YzA5Y2UxMGFjYmNjZjM0YzRiYTBiNWYxMzVkNjQzZGM1MzdkYTFmMWRmMzU1YTIyNWU4MiJ9fX0==";
    private static final String FALSE_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkxZDZlZGE4M2VkMmMyNGRjZGNjYjFlMzNkZjM2OTRlZWUzOTdhNTcwMTIyNTViZmM1NmEzYzI0NGJjYzQ3NCJ9fX0===";

    private static final String MODIFY_SUCCESS = MessageUtils.color("&aObject modified: &6%s: &e%s");

    public BooleanFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
                                             ModelPath<? extends Model> path, ChangeInformation changeInformation) {
        super(plugin, parent, path, changeInformation);
    }

    @Override
    protected void initializeInventory() {
        this.inventory = Bukkit.createInventory(this, 27,
                MessageUtils.color("&6&lModify &e&l" + changeInformation.getName()));
        registerClickConsumer(12, getBooleanItem(true), getBooleanConsumer(true));
        registerClickConsumer(14, getBooleanItem(false), getBooleanConsumer(false));
        registerClickConsumer(26, getBackItem(), getBackConsumer());
        fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
    }

    private Consumer<InventoryClickEvent> getBooleanConsumer(boolean value) {
        return e -> {
            HumanEntity p = e.getWhoClicked();
            changeInformation.setValue(path, value);
            p.sendMessage(String.format(MODIFY_SUCCESS, changeInformation.getName(), value));
            getBackConsumer().accept(e);
        };
    }

    private ItemStack getBooleanItem(boolean value) {
        return ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(value ? TRUE_HEAD : FALSE_HEAD),
                MessageUtils.color("&6&lModify -> &e&l" + value), new ArrayList<>());
    }

}
