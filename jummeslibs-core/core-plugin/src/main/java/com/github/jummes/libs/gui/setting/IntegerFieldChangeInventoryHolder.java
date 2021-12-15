package com.github.jummes.libs.gui.setting;

import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.setting.change.ChangeInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import org.bukkit.plugin.java.JavaPlugin;

public class IntegerFieldChangeInventoryHolder extends NumberFieldChangeInventoryHolder<Integer> {

    public IntegerFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path, ChangeInformation changeInformation) {
        super(plugin, parent, path, changeInformation);
    }

    public IntegerFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path, ChangeInformation changeInformation, Serializable.Number values) {
        super(plugin, parent, path, changeInformation, values);
    }

    @Override
    protected double getDefaultScale() {
        return 1;
    }

    @Override
    protected void assignResultValue(int value) {
        this.result = value;
    }

    @Override
    protected int compareTo(Integer result, int value) {
        return result.compareTo(value);
    }

    @Override
    protected Integer getZero() {
        return 0;
    }

    @Override
    protected Integer roundNumber(Integer operationResult) {
        return operationResult;
    }

    @Override
    protected Integer sum(Integer result, double addition) {
        return result + (int) addition;
    }
}
