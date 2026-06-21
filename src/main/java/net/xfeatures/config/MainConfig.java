package net.xfeatures.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class MainConfig {

    private final Plugin plugin;
    
    // Settings
    public String language;

    // Protection
    public boolean protectionEnabled;
    public List<String> blockedContainers;

    // Effects
    public boolean effectsEnabled;
    public boolean glowingEnabled;
    public String glowColor;
    public boolean particlesEnabled;
    public String particleType;
    public int particleCount;
    public int particleInterval;

    // Tracking
    public boolean trackingEnabled;
    public boolean persistent;

    public MainConfig(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();

        if (!config.contains("language")) {
            config.set("language", "ru");
        }
        language = config.getString("language", "ru");

        if (!config.contains("protection.enabled")) {
            config.set("protection.enabled", true);
        }
        protectionEnabled = config.getBoolean("protection.enabled", true);

        if (!config.contains("protection.blocked-containers")) {
            config.set("protection.blocked-containers", Arrays.asList(
                    "ENDER_CHEST", "SHULKER_BOX", "BUNDLE", "DISPENSER", "DROPPER",
                    "FURNACE", "BLAST_FURNACE", "SMOKER", "BREWING_STAND", "HOPPER",
                    "BARREL", "CRAFTER"
            ));
        }
        blockedContainers = config.getStringList("protection.blocked-containers");

        if (!config.contains("effects.enabled")) {
            config.set("effects.enabled", true);
        }
        effectsEnabled = config.getBoolean("effects.enabled", true);

        if (!config.contains("effects.glowing.enabled")) {
            config.set("effects.glowing.enabled", true);
        }
        glowingEnabled = config.getBoolean("effects.glowing.enabled", true);

        if (!config.contains("effects.glowing.color")) {
            config.set("effects.glowing.color", "LIGHT_PURPLE");
        }
        glowColor = config.getString("effects.glowing.color", "LIGHT_PURPLE");

        if (!config.contains("effects.particles.enabled")) {
            config.set("effects.particles.enabled", true);
        }
        particlesEnabled = config.getBoolean("effects.particles.enabled", true);

        if (!config.contains("effects.particles.type")) {
            config.set("effects.particles.type", "END_ROD");
        }
        particleType = config.getString("effects.particles.type", "END_ROD");

        if (!config.contains("effects.particles.count")) {
            config.set("effects.particles.count", 3);
        }
        particleCount = config.getInt("effects.particles.count", 3);

        if (!config.contains("effects.particles.interval")) {
            config.set("effects.particles.interval", 10);
        }
        particleInterval = config.getInt("effects.particles.interval", 10);

        if (!config.contains("tracking.enabled")) {
            config.set("tracking.enabled", true);
        }
        trackingEnabled = config.getBoolean("tracking.enabled", true);

        if (!config.contains("tracking.persistent")) {
            config.set("tracking.persistent", true);
        }
        persistent = config.getBoolean("tracking.persistent", true);

        plugin.saveConfig();
    }
}
