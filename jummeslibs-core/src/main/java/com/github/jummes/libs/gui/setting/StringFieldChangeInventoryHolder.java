package com.github.jummes.libs.gui.setting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.setting.change.ChangeInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

public class StringFieldChangeInventoryHolder extends FieldChangeInventoryHolder {

    private static final String MODIFY_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdiNjJkMjc1ZDg3YzA5Y2UxMGFjYmNjZjM0YzRiYTBiNWYxMzVkNjQzZGM1MzdkYTFmMWRmMzU1YTIyNWU4MiJ9fX0==";

    private static final String MODIFY_TITLE = "&6&lModify &e&l%s";
    private static final Component MODIFY_MESSAGE = MessageUtils.color(
            "&aTo modify the parameter type in chat the &6&lnew value&a.\n&aType &6&l'exit' &ato leave the value unmodified.");
    private static final Component MODIFY_ITEM_NAME = MessageUtils.color("&6&lModify Value");

    @Getter
    private static Set<StringFieldChangeInfo> changeStringInfoSet = new HashSet<StringFieldChangeInfo>();

    public StringFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
                                            ModelPath<? extends Model> path, ChangeInformation changeInformation) {
        super(plugin, parent, path, changeInformation);
    }

    @Override
    protected void initializeInventory() {
        this.inventory = Bukkit.createInventory(this, 27,
                MessageUtils.color(String.format(MODIFY_TITLE, changeInformation.getName())));

        registerClickConsumer(13, ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(MODIFY_HEAD),
                MODIFY_ITEM_NAME, new ArrayList<>()), e -> playerCanWrite(e.getWhoClicked()));
        registerClickConsumer(26, getBackItem(), getBackConsumer());
        fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
    }

    private void playerCanWrite(HumanEntity entity) {
        entity.sendMessage(MODIFY_MESSAGE);
        changeStringInfoSet.add(new StringFieldChangeInfo(entity, path, parent, changeInformation));
        entity.closeInventory();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    public class StringFieldChangeInfo {
        @EqualsAndHashCode.Include
        private HumanEntity human;
        private ModelPath<? extends Model> path;
        private InventoryHolder parent;
        private ChangeInformation changeInformation;
    }

}
