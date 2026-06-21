package net.xfeatures.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.xfeatures.XfeaturesDragonEgg;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesConfig {

    private final XfeaturesDragonEgg plugin;
    private final Map<String, String> cachedMessages = new HashMap<>();
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public MessagesConfig(XfeaturesDragonEgg plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void extractLanguageIfAvailable(String lang) {
        File langFile = new File(plugin.getDataFolder(), "messages/messages-" + lang + ".yml");
        if (!langFile.exists()) {
            if (plugin.getResource("messages/messages-" + lang + ".yml") != null) {
                plugin.saveResource("messages/messages-" + lang + ".yml", false);
            }
        }
    }

    public void loadConfig() {
        cachedMessages.clear();
        String lang = plugin.mainConfig.language;

        extractLanguageIfAvailable("en");
        if (!lang.equals("en")) {
            extractLanguageIfAvailable(lang);
        }

        File langFile = new File(plugin.getDataFolder(), "messages/messages-" + lang + ".yml");

        if (!langFile.exists()) {
            plugin.getLogger().warning("Language file messages-" + lang + ".yml not found! Falling back to messages-en.yml");
            langFile = new File(plugin.getDataFolder(), "messages/messages-en.yml");
            if (!langFile.exists()) {
                langFile = new File(plugin.getDataFolder(), "messages.yml");
                if (!langFile.exists()) {
                    plugin.saveResource("messages.yml", false);
                }
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(langFile);

        // Load defaults from messages.yml inside jar
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            
            boolean changed = false;
            for (String key : defaultConfig.getKeys(true)) {
                if (defaultConfig.isString(key)) {
                    if (!config.contains(key)) {
                        config.set(key, defaultConfig.getString(key));
                        changed = true;
                    }
                }
            }
            if (changed) {
                try {
                    config.save(langFile);
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not save language file " + langFile.getName());
                }
            }
        }

        for (String key : config.getKeys(true)) {
            if (config.isString(key)) {
                cachedMessages.put(key, config.getString(key));
            }
        }
    }

    public void reloadConfig() {
        loadConfig();
    }

    public String format(String path, Object... replacements) {
        return colorize(formatNoColor(path, replacements));
    }

    public String formatNoColor(String path, Object... replacements) {
        String message = cachedMessages.getOrDefault(path, "Missing message: " + path);

        // Replace global prefix
        if (cachedMessages.containsKey("prefix")) {
            message = message.replace("%prefix%", cachedMessages.get("prefix"));
        }

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                String placeholder = "%" + replacements[i] + "%";
                String value = String.valueOf(replacements[i + 1]);
                message = message.replace(placeholder, value);
            }
        }

        return message;
    }

    public String colorize(String input) {
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append("§").append(c);
            }
            matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    public Component formatAsComponent(String path, Object... replacements) {
        String message = formatNoColor(path, replacements);
        
        // First, handle hex colors &#RRGGBB -> <#RRGGBB>
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<#" + matcher.group(1) + ">");
        }
        matcher.appendTail(sb);
        
        // Deserialize the '&' legacy codes into a Component
        Component legacyComp = LegacyComponentSerializer.legacyAmpersand().deserialize(sb.toString());
        
        // Serialize the Component into a proper MiniMessage string (this converts colors like RED to <red>)
        String miniMessageString = MiniMessage.miniMessage().serialize(legacyComp);
        
        // Clean up escaped MiniMessage tags (since legacy serializer escapes '<' and '>')
        miniMessageString = miniMessageString.replace("\\<", "<").replace("\\>", ">");
        
        // Finally parse as MiniMessage
        return MiniMessage.miniMessage().deserialize(miniMessageString);
    }
}
