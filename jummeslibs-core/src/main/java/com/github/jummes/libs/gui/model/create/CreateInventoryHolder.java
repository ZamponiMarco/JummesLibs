package com.github.jummes.libs.gui.model.create;

import com.github.jummes.libs.gui.FieldInventoryHolderFactory;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.gui.setting.change.CollectionAddInformation;
import com.github.jummes.libs.gui.setting.change.FieldChangeInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import org.apache.commons.lang.reflect.ConstructorUtils;
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
                // Call beforeComponentCreation
                if (path.getLast() != null)
                    path.getLast().beforeComponentCreation(model);

                // Determine one of the suitable constructors
                Constructor<?> cons;
                Model newModel;
                if (ConstructorUtils.getAccessibleConstructor(model, new Class[0]) != null) {
                    cons = model.getConstructor();
                    newModel = (Model) cons.newInstance();
                } else if (ConstructorUtils.getAccessibleConstructor(model, new Class[]{Player.class}) != null) {
                    cons = model.getConstructor(Player.class);
                    newModel = (Model) cons.newInstance((Player) e.getWhoClicked());
                } else if (ConstructorUtils.getAccessibleConstructor(model, new Class[]{ModelPath.class}) != null) {
                    cons = model.getConstructor(ModelPath.class);
                    newModel = (Model) cons.newInstance(path);
                } else {
                    throw new NoSuchMethodException();
                }
                newModel.onCreation();

                // Call afterComponentCreation for father class
                if (path.getLast() != null)
                    path.getLast().afterComponentCreation(newModel);

                // Put the new model inside the saved data
                if (isCollection) {
                    new CollectionAddInformation(field).setValue(path, newModel);
                    e.getWhoClicked()
                            .openInventory(new ModelObjectInventoryHolder(plugin, parent, path).getInventory());
                } else {
                    path.getLast().beforeComponentSetting(newModel);
                    new FieldChangeInformation(field).setValue(path, newModel);
                    path.getLast().afterComponentSetting(newModel);
                    e.getWhoClicked().openInventory(FieldInventoryHolderFactory
                            .createFieldInventoryHolder(plugin, parent, path, field, e).getInventory());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }
}
