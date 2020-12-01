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

public class DoubleFieldChangeInventoryHolder extends NumberFieldChangeInventoryHolder<Double> {

    public DoubleFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path, ChangeInformation changeInformation) {
        super(plugin, parent, path, changeInformation);
    }

    public DoubleFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path, ChangeInformation changeInformation, Serializable.Number values) {
        super(plugin, parent, path, changeInformation, values);
    }

    @Override
    protected double getDefaultScale() {
        return 0.01;
    }

    @Override
    protected void assignResultValue(int value) {
        this.result = (double) value;
    }

    @Override
    protected int compareTo(Double result, int value) {
        return result.compareTo((double) value);
    }

    @Override
    protected Double getZero() {
        return 0d;
    }

    @Override
    protected Double roundNumber(Double operationResult) {
        return Math.round(operationResult * 100d) / 100d;
    }

    @Override
    protected Double sum(Double result, double addition) {
        return result + addition;
    }
}
