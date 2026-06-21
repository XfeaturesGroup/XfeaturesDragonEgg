package net.xfeatures.listener;

import net.xfeatures.XfeaturesDragonEgg;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class EggEffectListener implements Listener {

    private final XfeaturesDragonEgg plugin;
    private final Set<UUID> playersWithEgg = new HashSet<>();
    private Team glowTeam;

    public EggEffectListener(XfeaturesDragonEgg plugin) {
        this.plugin = plugin;

        if (plugin.mainConfig.effectsEnabled) {
            setupTeam();
            startTask();
        }
    }

    private void setupTeam() {
        if (!plugin.mainConfig.glowingEnabled) return;

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        glowTeam = scoreboard.getTeam("dragonegg_glow");
        if (glowTeam == null) {
            glowTeam = scoreboard.registerNewTeam("dragonegg_glow");
        }
        
        try {
            NamedTextColor color = NamedTextColor.NAMES.value(plugin.mainConfig.glowColor.toLowerCase());
            glowTeam.color(Objects.requireNonNullElse(color, NamedTextColor.LIGHT_PURPLE));
        } catch (Exception e) {
            glowTeam.color(NamedTextColor.LIGHT_PURPLE);
        }
    }

    private void startTask() {
        int rawInterval = plugin.mainConfig.particleInterval;
        final int interval = rawInterval < 1 ? 10 : rawInterval;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!plugin.mainConfig.effectsEnabled) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                boolean hasEgg = player.getInventory().contains(Material.DRAGON_EGG);
                UUID uuid = player.getUniqueId();

                if (hasEgg && player.hasPermission("xfeatures.dragonegg.effects")) {
                    if (!playersWithEgg.contains(uuid)) {
                        playersWithEgg.add(uuid);
                        if (plugin.mainConfig.glowingEnabled && glowTeam != null) {
                            glowTeam.addEntry(player.getName());
                        }
                    }

                    // Apply glow effect
                    if (plugin.mainConfig.glowingEnabled) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, interval + 20, 0, false, false, false));
                    }

                    // Spawn particles
                    if (plugin.mainConfig.particlesEnabled) {
                        Particle particle;
                        try {
                            particle = Particle.valueOf(plugin.mainConfig.particleType.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            particle = Particle.END_ROD;
                        }
                        player.getWorld().spawnParticle(
                                particle,
                                player.getLocation().add(0, 1, 0),
                                plugin.mainConfig.particleCount,
                                0.5, 0.5, 0.5, 0.02
                        );
                    }
                } else {
                    removeEffects(player);
                }
            }
        }, interval, interval);
    }

    private void removeEffects(Player player) {
        if (playersWithEgg.remove(player.getUniqueId())) {
            player.removePotionEffect(PotionEffectType.GLOWING);
            if (glowTeam != null) {
                glowTeam.removeEntry(player.getName());
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Nothing needed here, the timer task will pick it up
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeEffects(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!plugin.mainConfig.effectsEnabled) return;
        Player player = (Player) event.getPlayer();
        if (!player.getInventory().contains(Material.DRAGON_EGG)) {
            removeEffects(player);
        }
    }
}
