package net.xfeatures.listener;

import net.xfeatures.XfeaturesDragonEgg;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EggTrackingListener implements Listener {

    private final XfeaturesDragonEgg plugin;

    public EggTrackingListener(XfeaturesDragonEgg plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.mainConfig.trackingEnabled) return;
        
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        // Player picks up egg into cursor, or clicks on egg
        if (current != null && current.getType() == Material.DRAGON_EGG || cursor.getType() == Material.DRAGON_EGG) {
            
            // Check if it's going to player's inventory
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.getInventory().contains(Material.DRAGON_EGG)) {
                    plugin.eggStateData.setPlayerState(player);
                }
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!plugin.mainConfig.trackingEnabled) return;

        Inventory topInventory = event.getView().getTopInventory();
        if (topInventory.contains(Material.DRAGON_EGG)) {
            if (topInventory.getLocation() != null) {
                plugin.eggStateData.setChestState(topInventory.getLocation(), (Player) event.getPlayer());
            }
        }
        
        // Also verify player's inventory
        if (event.getPlayer().getInventory().contains(Material.DRAGON_EGG)) {
            plugin.eggStateData.setPlayerState((Player) event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!plugin.mainConfig.trackingEnabled) return;

        if (event.getEntity() instanceof Player player) {
            if (event.getItem().getItemStack().getType() == Material.DRAGON_EGG) {
                plugin.eggStateData.setPlayerState(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!plugin.mainConfig.trackingEnabled) return;

        if (event.getItemDrop().getItemStack().getType() == Material.DRAGON_EGG) {
            plugin.eggStateData.setDroppedState(event.getItemDrop().getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.mainConfig.trackingEnabled) return;

        if (event.getBlock().getType() == Material.DRAGON_EGG) {
            plugin.eggStateData.setBlockState(event.getBlock().getLocation(), event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.mainConfig.trackingEnabled) return;

        if (event.getBlock().getType() == Material.DRAGON_EGG) {
            plugin.eggStateData.setDroppedState(event.getBlock().getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.mainConfig.trackingEnabled) return;

        Player player = event.getPlayer();
        if (player.getInventory().contains(Material.DRAGON_EGG)) {
            plugin.eggStateData.setPlayerState(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Prevent dragon egg teleportation when clicked
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.DRAGON_EGG) {
                // If they are holding a block to place, let them place it against the egg
                if (!event.getPlayer().isSneaking() || event.getItem() == null || !event.getItem().getType().isBlock()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemDespawn(org.bukkit.event.entity.ItemDespawnEvent event) {
        if (event.getEntity().getItemStack().getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(org.bukkit.event.entity.EntityDamageEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Item item) {
            if (item.getItemStack().getType() == Material.DRAGON_EGG) {
                event.setCancelled(true);
            }
        }
    }
}
