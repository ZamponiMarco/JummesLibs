package com.github.jummes.libs.gui.model.create;

import com.github.jummes.libs.gui.FieldInventoryHolderFactory;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.setting.change.CollectionAddInformation;
import com.github.jummes.libs.gui.setting.change.FieldChangeInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ReflectUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public abstract class CreateInventoryHolder extends ModelObjectInventoryHolder {

    protected final Field field;
    protected final Class<? extends Model> modelClass;
    protected final boolean isCollection;

    public CreateInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent, ModelPath<? extends Model> path,
                                 Field field, Class<? extends Model> modelClass, boolean isCollection) {
        super(plugin, parent, path);
        this.field = field;
        this.modelClass = modelClass;
        this.isCollection = isCollection;
    }


    protected Consumer<InventoryClickEvent> getModelCreateConsumer(Class<? extends Model> model, boolean isCollection) {
        return e -> {
            try {

                // Determine one of the suitable constructors
                Constructor<?> cons;
                Model newModel;
                if (ReflectUtils.getAccessibleConstructor(model) != null) {
                    cons = model.getConstructor();
                    newModel = (Model) cons.newInstance();
                } else if (ReflectUtils.getAccessibleConstructor(model, Player.class) != null) {
                    cons = model.getConstructor(Player.class);
                    newModel = (Model) cons.newInstance((Player) e.getWhoClicked());
                } else if (ReflectUtils.getAccessibleConstructor(model, ModelPath.class) != null) {
                    cons = model.getConstructor(ModelPath.class);
                    newModel = (Model) cons.newInstance(path);
                } else {
                    throw new NoSuchMethodException();
                }
                newModel.onCreation();


                // Put the new model inside the saved data
                if (isCollection) {
                    new CollectionAddInformation(field).setValue(path, newModel);
                    e.getWhoClicked()
                            .openInventory(new ModelObjectInventoryHolder(plugin, parent, path).getInventory());
                } else {
                    new FieldChangeInformation(field).setValue(path, newModel);
                    e.getWhoClicked().openInventory(FieldInventoryHolderFactory
                            .createFieldInventoryHolder(plugin, parent, path, field, e).getInventory());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }
}
