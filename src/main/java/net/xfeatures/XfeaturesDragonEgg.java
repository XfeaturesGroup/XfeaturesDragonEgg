package net.xfeatures;

import net.xfeatures.command.DragonEggCommand;
import net.xfeatures.config.MainConfig;
import net.xfeatures.config.MessagesConfig;
import net.xfeatures.data.EggStateData;
import net.xfeatures.listener.EggContainerListener;
import net.xfeatures.listener.EggEffectListener;
import net.xfeatures.listener.EggTrackingListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public final class XfeaturesDragonEgg extends JavaPlugin {

    public static XfeaturesDragonEgg instance;
    public MainConfig mainConfig;
    public MessagesConfig messagesConfig;
    public EggStateData eggStateData;
    private Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        
        loadConfigs();

        // Initialize bStats
        int pluginId = 32137; // User's real bStats ID
        metrics = new Metrics(this, pluginId);
        setupCustomCharts();

        eggStateData = new EggStateData(this);

        // Register commands
        DragonEggCommand command = new DragonEggCommand(this);
        getCommand("dragonegg").setExecutor(command);
        getCommand("dragonegg").setTabCompleter(command);

        // Register listeners
        getServer().getPluginManager().registerEvents(new EggContainerListener(this), this);
        getServer().getPluginManager().registerEvents(new EggEffectListener(this), this);
        getServer().getPluginManager().registerEvents(new EggTrackingListener(this), this);

        getLogger().info("XfeaturesDragonEgg has been enabled!");
    }

    @Override
    public void onDisable() {
        if (eggStateData != null) {
            eggStateData.save();
        }
        getLogger().info("XfeaturesDragonEgg has been disabled!");
    }

    public void loadConfigs() {
        mainConfig = new MainConfig(this);
        messagesConfig = new MessagesConfig(this);
    }

    public void reloadConfigs() {
        reloadConfig();
        mainConfig.loadConfig();
        messagesConfig.reloadConfig();
    }

    private void setupCustomCharts() {
        try {
            metrics.addCustomChart(new SimplePie("protection_enabled", () ->
                mainConfig.protectionEnabled ? "Enabled" : "Disabled"));

            metrics.addCustomChart(new SimplePie("effects_enabled", () ->
                mainConfig.effectsEnabled ? "Enabled" : "Disabled"));

            metrics.addCustomChart(new SimplePie("tracking_enabled", () ->
                mainConfig.trackingEnabled ? "Enabled" : "Disabled"));
                
            metrics.addCustomChart(new SimplePie("persistent_tracking", () ->
                mainConfig.persistent ? "Enabled" : "Disabled"));
        } catch (Exception e) {
            getLogger().warning("Error when setting up bStats charts: " + e.getMessage());
        }
    }
}
