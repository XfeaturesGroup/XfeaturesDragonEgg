package net.xfeatures.command;

import net.xfeatures.XfeaturesDragonEgg;
import net.xfeatures.data.EggStateData;
import net.xfeatures.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DragonEggCommand implements CommandExecutor, TabCompleter {

    private final XfeaturesDragonEgg plugin;

    public DragonEggCommand(XfeaturesDragonEgg plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "where" -> handleWhere(sender);
            case "language" -> handleLanguage(sender, args);
            case "reload" -> handleReload(sender);
            case "help" -> sendHelp(sender);
            default -> sender.sendMessage(plugin.messagesConfig.formatAsComponent("unknown-command"));
        }

        return true;
    }

    private void handleWhere(CommandSender sender) {
        if (!sender.hasPermission("xfeatures.dragonegg.where")) {
            sender.sendMessage(plugin.messagesConfig.formatAsComponent("no-permission"));
            return;
        }

        EggStateData data = plugin.eggStateData;
        if (data.getType() == EggStateData.EggLocationType.UNKNOWN || data.getType() == null) {
            sender.sendMessage(plugin.messagesConfig.formatAsComponent("where-not-found"));
            return;
        }

        sender.sendMessage(plugin.messagesConfig.formatAsComponent("where-header"));

        String duration = TimeUtil.formatDuration(System.currentTimeMillis() - data.getSince(), plugin.messagesConfig);

        switch (data.getType()) {
            case INVENTORY -> {
                sender.sendMessage(plugin.messagesConfig.formatAsComponent("where-in-inventory",
                        "player", data.getPlayerName(),
                        "x", data.getX(),
                        "y", data.getY(),
                        "z", data.getZ(),
                        "world", data.getWorld()
                ));
                
                boolean online = false;
                if (data.getPlayerUUID() != null) {
                    Player p = Bukkit.getPlayer(data.getPlayerUUID());
                    if (p != null && p.isOnline()) online = true;
                }
                
                if (online) {
                    sender.sendMessage(plugin.messagesConfig.formatAsComponent("where-player-online"));
                } else {
                    sender.sendMessage(plugin.messagesConfig.formatAsComponent("where-player-offline"));
                }
            }
            case CHEST -> {
                sender.sendMessage(plugin.messagesConfig.formatAsComponent("where-in-chest",
                        "x", data.getX(),
                        "y", data.getY(),
                        "z", data.getZ(),
                        "world", data.getWorld()
                ));
                sender.sendMessage(plugin.messagesConfig.formatAsComponent("where-last-touched-by", "player", data.getPlayerName()));
            }
            case BLOCK -> {
                sender.sendMessage(plugin.messagesConfig.formatAsComponent("where-as-block",
                        "x", data.getX(),
                        "y", data.getY(),
                        "z", data.getZ(),
                        "world", data.getWorld()
                ));
                sender.sendMessage(plugin.messagesConfig.formatAsComponent("where-placed-by", "player", data.getPlayerName()));
            }
            case DROPPED -> {
                sender.sendMessage(plugin.messagesConfig.formatAsComponent("where-dropped",
                        "x", data.getX(),
                        "y", data.getY(),
                        "z", data.getZ(),
                        "world", data.getWorld()
                ));
            }
        }

        sender.sendMessage(plugin.messagesConfig.formatAsComponent("where-duration", "duration", duration));
    }

    private void handleLanguage(CommandSender sender, String[] args) {
        if (!sender.hasPermission("xfeatures.dragonegg.language")) {
            sender.sendMessage(plugin.messagesConfig.formatAsComponent("no-permission"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.messagesConfig.formatAsComponent("language-usage"));
            return;
        }

        String lang = args[1].toLowerCase();
        File langFile = new File(plugin.getDataFolder(), "messages/messages-" + lang + ".yml");
        
        // Also check if we have it in jar but not extracted yet
        if (!langFile.exists() && plugin.getResource("messages/messages-" + lang + ".yml") == null) {
            sender.sendMessage(plugin.messagesConfig.formatAsComponent("language-not-found", "language", lang));
            return;
        }

        plugin.getConfig().set("language", lang);
        plugin.saveConfig();
        plugin.reloadConfigs();
        
        sender.sendMessage(plugin.messagesConfig.formatAsComponent("language-changed", "language", lang));
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("xfeatures.dragonegg.reload")) {
            sender.sendMessage(plugin.messagesConfig.formatAsComponent("no-permission"));
            return;
        }

        plugin.reloadConfigs();
        sender.sendMessage(plugin.messagesConfig.formatAsComponent("reload-success"));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.messagesConfig.formatAsComponent("help-header"));
        
        if (sender.hasPermission("xfeatures.dragonegg.where")) {
            sender.sendMessage(plugin.messagesConfig.formatAsComponent("help-where"));
        }
        if (sender.hasPermission("xfeatures.dragonegg.language")) {
            sender.sendMessage(plugin.messagesConfig.formatAsComponent("help-language"));
        }
        if (sender.hasPermission("xfeatures.dragonegg.reload")) {
            sender.sendMessage(plugin.messagesConfig.formatAsComponent("help-reload"));
        }
        sender.sendMessage(plugin.messagesConfig.formatAsComponent("help-help"));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("help");
            if (sender.hasPermission("xfeatures.dragonegg.where")) completions.add("where");
            if (sender.hasPermission("xfeatures.dragonegg.language")) completions.add("language");
            if (sender.hasPermission("xfeatures.dragonegg.reload")) completions.add("reload");
            
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("language") && sender.hasPermission("xfeatures.dragonegg.language")) {
            completions.add("ar");
            completions.add("cn");
            completions.add("de");
            completions.add("en");
            completions.add("es");
            completions.add("et");
            completions.add("fr");
            completions.add("hi");
            completions.add("it");
            completions.add("ja");
            completions.add("ko");
            completions.add("lt");
            completions.add("lv");
            completions.add("pl");
            completions.add("pt");
            completions.add("ru");
            completions.add("tr");
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
