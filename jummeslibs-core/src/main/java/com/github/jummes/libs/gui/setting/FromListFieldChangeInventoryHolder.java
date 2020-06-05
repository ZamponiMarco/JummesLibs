package com.github.jummes.libs.gui.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.setting.change.ChangeInformation;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;
import com.google.common.collect.Lists;

public class FromListFieldChangeInventoryHolder extends FieldChangeInventoryHolder {

    private static final String NEXT_PAGE_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTdiMDNiNzFkM2Y4NjIyMGVmMTIyZjk4MzFhNzI2ZWIyYjI4MzMxOWM3YjYyZTdkY2QyZDY0ZDk2ODIifX19==";
    private static final String PREVIOUS_PAGE_ITEM = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDgzNDhhYTc3ZjlmYjJiOTFlZWY2NjJiNWM4MWI1Y2EzMzVkZGVlMWI5MDVmM2E4YjkyMDk1ZDBhMWYxNDEifX19==";

    private static final int OBJECTS_NUMBER = 51;

    private int page;
    private List<Object> objects;
    private Function<Object, ItemStack> mapper;

    public FromListFieldChangeInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
                                              ModelPath<? extends Model> path, ChangeInformation changeInformation, int page, List<Object> objects,
                                              Function<Object, ItemStack> mapper) {
        super(plugin, parent, path, changeInformation);
        this.page = page;
        this.objects = objects;
        this.mapper = mapper;
    }

    @Override
    protected void initializeInventory() {
        /*
         * Get the list of models to display in the current page
         */
        try {
            List<Object> toList = objects.stream().filter(model -> objects.indexOf(model) >= (page - 1) * OBJECTS_NUMBER
                    && objects.indexOf(model) <= page * OBJECTS_NUMBER - 1).collect(Collectors.toList());
            int maxPage = (int) Math.ceil((objects.size() > 0 ? objects.size() : 1) / (double) OBJECTS_NUMBER);

            /*
             * Create the inventory
             */
            this.inventory = Bukkit.createInventory(this, 54, MessageUtils.color("&cChoose from these &6&l(&c" + page + "&6&l/&c" + maxPage + "&6&l)"));

            /*
             * For each model that has to be listed set an item in the GUI
             */
            toList.forEach(object -> {
                registerClickConsumer(toList.indexOf(object),
                        mapper == null
                                ? ItemUtils.getNamedItem(new ItemStack(Material.CARROT), object.toString(),
                                Lists.newArrayList())
                                : mapper.apply(object),
                        e -> {
                            try {
                                /*
                                 * If left clicked open the model GUI inventory
                                 */
                                if (e.getClick().equals(ClickType.LEFT)) {
                                    changeInformation.setValue(path, object);
                                    e.getWhoClicked().openInventory(parent.getInventory());
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
            });
            if (page != maxPage) {
                registerClickConsumer(52,
                        ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(NEXT_PAGE_ITEM),
                                MessageUtils.color("&6&lNext page"), new ArrayList<String>()),
                        e -> e.getWhoClicked().openInventory(new FromListFieldChangeInventoryHolder(plugin, parent,
                                path, changeInformation, page + 1, objects, mapper).getInventory()));
            }
            if (page != 1) {
                registerClickConsumer(51,
                        ItemUtils.getNamedItem(Libs.getWrapper().skullFromValue(PREVIOUS_PAGE_ITEM),
                                MessageUtils.color("&6&lPrevious page"), new ArrayList<String>()),
                        e -> e.getWhoClicked().openInventory(new FromListFieldChangeInventoryHolder(plugin, parent,
                                path, changeInformation, page - 1, objects, mapper).getInventory()));
            }
            registerClickConsumer(53, getBackItem(), getBackConsumer());
            fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
