package net.xfeatures.listener;

import net.xfeatures.XfeaturesDragonEgg;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EggContainerListener implements Listener {

    private final XfeaturesDragonEgg plugin;

    public EggContainerListener(XfeaturesDragonEgg plugin) {
        this.plugin = plugin;
    }

    private boolean isBlockedContainer(Inventory inventory) {
        if (!plugin.mainConfig.protectionEnabled) return false;
        
        InventoryType type = inventory.getType();
        List<String> blocked = plugin.mainConfig.blockedContainers;
        
        // Always allow player inventory
        if (type == InventoryType.PLAYER || type == InventoryType.CREATIVE) {
            return false;
        }

        // Check against the blocked list
        if (blocked.contains(type.name())) {
            return true;
        }

        // Check shulker boxes by material type (holder)
        if (inventory.getHolder() != null) {
            String holderName = inventory.getHolder().getClass().getSimpleName().toUpperCase();
            if (holderName.contains("SHULKER")) {
                return blocked.contains("SHULKER_BOX");
            }
        }
        
        return false;
    }

    private void sendMessage(Player player, Inventory inventory) {
        String containerName = inventory.getType().name();
        if (inventory.getHolder() != null && inventory.getHolder().getClass().getSimpleName().toUpperCase().contains("SHULKER")) {
            containerName = "SHULKER_BOX";
        }
        player.sendMessage(plugin.messagesConfig.formatAsComponent("container-blocked", "container", containerName));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.mainConfig.protectionEnabled) return;
        
        Player player = (Player) event.getWhoClicked();
        if (player.hasPermission("xfeatures.dragonegg.bypass")) return;

        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        ClickType click = event.getClick();
        
        Inventory topInventory = event.getView().getTopInventory();
        Inventory clickedInventory = event.getClickedInventory();

        boolean isBlockedTop = isBlockedContainer(topInventory);

        // Scenario 1: Shift-clicking from player inventory into a blocked container
        if (click.isShiftClick() && clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER) {
            if (current != null && current.getType() == Material.DRAGON_EGG) {
                if (isBlockedTop) {
                    event.setCancelled(true);
                    sendMessage(player, topInventory);
                    return;
                }
            }
        }

        // Scenario 2: Placing egg with cursor into a blocked container
        if (clickedInventory != null && isBlockedContainer(clickedInventory)) {
            if (cursor.getType() == Material.DRAGON_EGG) {
                event.setCancelled(true);
                sendMessage(player, clickedInventory);
                return;
            }
            
            // Number key swap
            if (click == ClickType.NUMBER_KEY) {
                ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
                if (hotbarItem != null && hotbarItem.getType() == Material.DRAGON_EGG) {
                    event.setCancelled(true);
                    sendMessage(player, clickedInventory);
                    return;
                }
            }
        }
        
        // Scenario 3: Bundle handling
        // 1. Right-clicking bundle with dragon egg in cursor
        if (current != null && current.getType() == Material.BUNDLE && cursor.getType() == Material.DRAGON_EGG) {
            if (plugin.mainConfig.blockedContainers.contains("BUNDLE")) {
                event.setCancelled(true);
                player.sendMessage(plugin.messagesConfig.formatAsComponent("container-blocked", "container", "BUNDLE"));
                return;
            }
        }
        // 2. Right-clicking dragon egg with bundle in cursor
        if (current != null && current.getType() == Material.DRAGON_EGG && cursor.getType() == Material.BUNDLE) {
            if (plugin.mainConfig.blockedContainers.contains("BUNDLE")) {
                event.setCancelled(true);
                player.sendMessage(plugin.messagesConfig.formatAsComponent("container-blocked", "container", "BUNDLE"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!plugin.mainConfig.protectionEnabled) return;
        
        Player player = (Player) event.getWhoClicked();
        if (player.hasPermission("xfeatures.dragonegg.bypass")) return;

        ItemStack cursor = event.getOldCursor();
        if (cursor.getType() != Material.DRAGON_EGG) return;

        Inventory topInventory = event.getView().getTopInventory();
        if (isBlockedContainer(topInventory)) {
            for (int slot : event.getRawSlots()) {
                if (slot < topInventory.getSize()) {
                    event.setCancelled(true);
                    sendMessage(player, topInventory);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (!plugin.mainConfig.protectionEnabled) return;
        
        if (event.getItem().getType() != Material.DRAGON_EGG) return;

        Inventory destination = event.getDestination();
        if (isBlockedContainer(destination)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        if (!plugin.mainConfig.protectionEnabled) return;
        
        if (event.getItem().getType() == Material.DRAGON_EGG) {
            // Dispensers shouldn't dispense it if blocked
            if (plugin.mainConfig.blockedContainers.contains("DISPENSER") && event.getBlock().getType() == Material.DISPENSER) {
                event.setCancelled(true);
            }
            if (plugin.mainConfig.blockedContainers.contains("DROPPER") && event.getBlock().getType() == Material.DROPPER) {
                event.setCancelled(true);
            }
        }
    }
}
