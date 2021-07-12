package com.github.jummes.libs.gui.model;

import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class RemoveConfirmationInventoryHolder extends PluginInventoryHolder {

    private static final String CONFIRM_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdiNjJkMjc1ZDg3YzA5Y2UxMGFjYmNjZjM0YzRiYTBiNWYxMzVkNjQzZGM1MzdkYTFmMWRmMzU1YTIyNWU4MiJ9fX0=";

    private static final String CONFIRM_ITEM = MessageUtils.color("&6&lConfirm");

    protected ModelPath<?> path;
    protected List<? extends Model> models;
    protected Field field;

    public RemoveConfirmationInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<?> path,
                                             Model model, Field field) {
        this(plugin, parent, path, Lists.newArrayList(model), field);
    }

    public RemoveConfirmationInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<?> path,
                                             List<? extends Model> models, Field field) {
        super(plugin, parent);
        this.path = path;
        this.models = models;
        this.field = field;
    }

    @Override
    protected void initializeInventory() {
        this.inventory = Bukkit.createInventory(this, 27, MessageUtils.color("&6&lConfirm &c&ldeletion"));
        registerClickConsumer(13, ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(CONFIRM_HEAD),
                CONFIRM_ITEM, Lists.newArrayList()), e -> {
            try {
                ((Collection<?>) FieldUtils.readField(field,
                        path.getLast() != null ? path.getLast() : path.getModelManager(), true)).removeAll(models);
                models.forEach(model -> {
                    if (path.getLast() == null) {
                        path.deleteRoot(model);
                    } else {
                        path.saveModel(field);
                    }
                });
                e.getWhoClicked().openInventory(parent.getInventory());
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        });
        registerClickConsumer(26, getBackItem(), getBackConsumer());
        fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
    }
}
