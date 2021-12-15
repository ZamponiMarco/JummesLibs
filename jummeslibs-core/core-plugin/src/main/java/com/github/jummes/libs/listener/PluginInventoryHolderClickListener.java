package com.github.jummes.libs.listener;

import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.util.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.HashMap;
import java.util.Map;

public class PluginInventoryHolderClickListener implements Listener {

    private static final Component SAVED_MESSAGE = MessageUtils.color("&aThe inventory has been saved. " +
            "&6&l[F] &ato open it again.");

    private final Map<HumanEntity, PluginInventoryHolder> savedInventories;

    public PluginInventoryHolderClickListener() {
        this.savedInventories = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) {
            return;
        }

        if (e.getClickedInventory().getHolder() instanceof PluginInventoryHolder) {
            if (e.getClick().equals(ClickType.SWAP_OFFHAND)) {
                savedInventories.put(e.getWhoClicked(), (PluginInventoryHolder) e.getClickedInventory().getHolder());
                e.getWhoClicked().closeInventory();
                ((Player) e.getWhoClicked()).updateInventory();
                e.getWhoClicked().sendMessage(SAVED_MESSAGE);
            } else {
                ((PluginInventoryHolder) e.getClickedInventory().getHolder()).handleClickEvent(e);
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHandsSwap(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        if (savedInventories.containsKey(player)) {
            e.setCancelled(true);
            player.openInventory(savedInventories.get(player).getInventory());
            savedInventories.remove(player);
        }
    }

}
