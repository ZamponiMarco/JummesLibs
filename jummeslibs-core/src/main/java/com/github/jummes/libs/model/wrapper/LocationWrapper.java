package com.github.jummes.libs.model.wrapper;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.annotation.CustomClickable;
import com.github.jummes.libs.annotation.GUINameable;
import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.annotation.SetterMappable;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.model.ModelObjectInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.MessageUtils;

@GUINameable(stringValue = true)
@CustomClickable(customFieldClickConsumer = "getCustomClickConsumer")
public class LocationWrapper extends ModelWrapper<Location> implements Model {

    private static final String X_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkxZDZlZGE4M2VkMmMyNGRjZGNjYjFlMzNkZjM2OTRlZWUzOTdhNTcwMTIyNTViZmM1NmEzYzI0NGJjYzQ3NCJ9fX0=";
    private static final String Y_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODlmZjhjNzQ0OTUwNzI5ZjU4Y2I0ZTY2ZGM2OGVhZjYyZDAxMDZmOGE1MzE1MjkxMzNiZWQxZDU1ZTMifX19=";
    private static final String Z_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzA1ZjE4ZDQxNmY2OGU5YmQxOWQ1NWRmOWZhNzQyZWRmYmYxYTUyNWM4ZTI5ZjY1OWFlODMzYWYyMTdkNTM1In19fQ===";
    private static final String YAW_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0====";
    private static final String PITCH_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk5YWFmMjQ1NmE2MTIyZGU4ZjZiNjI2ODNmMmJjMmVlZDlhYmI4MWZkNWJlYTFiNGMyM2E1ODE1NmI2NjkifX19===";

    @Serializable(headTexture = Y_HEAD)
    @SetterMappable(setterMethod = "setY")
    private double y;
    @Serializable(headTexture = X_HEAD)
    @SetterMappable(setterMethod = "setX")
    private double x;
    @Serializable(headTexture = Z_HEAD)
    @SetterMappable(setterMethod = "setZ")
    private double z;
    @Serializable(headTexture = YAW_HEAD)
    @SetterMappable(setterMethod = "setYaw")
    private float yaw;
    @Serializable(headTexture = PITCH_HEAD)
    @SetterMappable(setterMethod = "setPitch")
    private float pitch;

    public LocationWrapper(Location wrapped) {
        super(wrapped);
        this.x = wrapped.getX();
        this.y = wrapped.getY();
        this.z = wrapped.getZ();
        this.yaw = wrapped.getYaw();
        this.pitch = wrapped.getPitch();
    }

    @SuppressWarnings("unused")
    public PluginInventoryHolder getCustomClickConsumer(JavaPlugin plugin, PluginInventoryHolder parent,
                                                        ModelPath<? extends Model> path, Field field, InventoryClickEvent e) {
        if (e.getClick().equals(ClickType.LEFT)) {
            path.addModel(this);
            return new ModelObjectInventoryHolder(plugin, parent, path);
        } else if (e.getClick().equals(ClickType.RIGHT)) {
            Block b = e.getWhoClicked().rayTraceBlocks(30).getHitBlock();
            if (b != null) {
                this.wrapped = b.getLocation();
                this.x = wrapped.getX();
                this.y = wrapped.getY();
                this.z = wrapped.getZ();
                this.yaw = wrapped.getYaw();
                this.pitch = wrapped.getPitch();
            }
            return parent;
        }
        return null;
    }

    @Override
    public ItemStack getGUIItem() {
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        return wrapped.serialize();
    }

    public static LocationWrapper deserialize(Map<String, Object> map) {
        return new LocationWrapper(Location.deserialize(map));
    }

    @Override
    public String toString() {
        NumberFormat f = new DecimalFormat("#0.00");
        return MessageUtils
                .color(String.format("&c%s&6/&c%s&6/&c%s&6/&c%s", wrapped.getWorld().getName(), f.format(x), f.format(y), f.format(z)));
    }

}
