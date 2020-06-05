package com.github.jummes.libs.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.github.jummes.libs.gui.PluginInventoryHolder;

public class PluginInventoryHolderClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) {
            return;
        }

        if (e.getClickedInventory().getHolder() instanceof PluginInventoryHolder) {
            ((PluginInventoryHolder) e.getClickedInventory().getHolder()).handleClickEvent(e);
        }
    }

}
