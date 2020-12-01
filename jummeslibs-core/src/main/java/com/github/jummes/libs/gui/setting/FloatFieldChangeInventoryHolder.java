package com.github.jummes.libs.gui.setting;

import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.setting.change.ChangeInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import org.bukkit.plugin.java.JavaPlugin;

public class FloatFieldChangeInventoryHolder extends NumberFieldChangeInventoryHolder<Float> {

    public FloatFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path, ChangeInformation changeInformation) {
        super(plugin, parent, path, changeInformation);
    }

    public FloatFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path, ChangeInformation changeInformation, Serializable.Number values) {
        super(plugin, parent, path, changeInformation, values);
    }

    @Override
    protected double getDefaultScale() {
        return 0.01;
    }

    @Override
    protected void assignResultValue(int value) {
        this.result = (float) value;
    }

    @Override
    protected int compareTo(Float result, int value) {
        return result.compareTo((float) value);
    }

    @Override
    protected Float getZero() {
        return 0f;
    }

    @Override
    protected Float roundNumber(Float operationResult) {
        return Math.round(operationResult * 100f) / 100f;
    }

    @Override
    protected Float sum(Float result, double addition) {
        return result + (float) addition;
    }
}
