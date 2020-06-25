package com.github.jummes.libs.gui.model.create;

import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Class that handles the creation of new Model objects throught the use of a
 * GUI
 *
 * @author Marco
 */
public class ModelCreateInventoryHolder extends CreateInventoryHolder {

    private static final String CONFIRM_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdiNjJkMjc1ZDg3YzA5Y2UxMGFjYmNjZjM0YzRiYTBiNWYxMzVkNjQzZGM1MzdkYTFmMWRmMzU1YTIyNWU4MiJ9fX0";

    public ModelCreateInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path,
                                      Field field, Class<? extends Model> modelClass, boolean isCollection) {
        super(plugin, parent, path, field, modelClass, isCollection);
    }

    @Override
    protected void initializeInventory() {
        this.inventory = Bukkit.createInventory(this, 27,
                MessageUtils.color("&6Create a &c&l" + modelClass.getSimpleName()));
        registerClickConsumer(13,
                ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(CONFIRM_HEAD),
                        MessageUtils.color("&6&lConfirm"), new ArrayList<String>()),
                getModelCreateConsumer(modelClass, isCollection));
        registerClickConsumer(26, getBackItem(), getBackConsumer());
        fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
    }

}
