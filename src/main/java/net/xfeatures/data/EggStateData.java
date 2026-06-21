package net.xfeatures.data;

import net.xfeatures.XfeaturesDragonEgg;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class EggStateData {

    public enum EggLocationType {
        INVENTORY, CHEST, BLOCK, DROPPED, UNKNOWN
    }

    private final XfeaturesDragonEgg plugin;
    private final File dataFile;
    private YamlConfiguration config;

    private EggLocationType type = EggLocationType.UNKNOWN;
    private UUID playerUUID = null;
    private String playerName = "Unknown";
    private String world = "";
    private int x = 0, y = 0, z = 0;
    private long since = 0;

    public EggStateData(XfeaturesDragonEgg plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data/egg-state.yml");
        load();
    }

    public void setPlayerState(Player player) {
        this.type = EggLocationType.INVENTORY;
        this.playerUUID = player.getUniqueId();
        this.playerName = player.getName();
        this.world = player.getWorld().getName();
        this.x = player.getLocation().getBlockX();
        this.y = player.getLocation().getBlockY();
        this.z = player.getLocation().getBlockZ();
        this.since = System.currentTimeMillis();
        save();
    }

    public void setChestState(Location loc, Player lastTouched) {
        this.type = EggLocationType.CHEST;
        if (lastTouched != null) {
            this.playerUUID = lastTouched.getUniqueId();
            this.playerName = lastTouched.getName();
        }
        if (loc != null && loc.getWorld() != null) {
            this.world = loc.getWorld().getName();
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
        }
        this.since = System.currentTimeMillis();
        save();
    }

    public void setBlockState(Location loc, Player placedBy) {
        this.type = EggLocationType.BLOCK;
        if (placedBy != null) {
            this.playerUUID = placedBy.getUniqueId();
            this.playerName = placedBy.getName();
        }
        if (loc != null && loc.getWorld() != null) {
            this.world = loc.getWorld().getName();
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
        }
        this.since = System.currentTimeMillis();
        save();
    }

    public void setDroppedState(Location loc) {
        this.type = EggLocationType.DROPPED;
        if (loc != null && loc.getWorld() != null) {
            this.world = loc.getWorld().getName();
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
        }
        this.since = System.currentTimeMillis();
        save();
    }

    public void setUnknown() {
        this.type = EggLocationType.UNKNOWN;
        this.since = System.currentTimeMillis();
        save();
    }

    public EggLocationType getType() {
        return type;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public long getSince() {
        return since;
    }

    public void load() {
        if (!dataFile.exists()) {
            return;
        }

        config = YamlConfiguration.loadConfiguration(dataFile);
        if (config.contains("state.type")) {
            try {
                this.type = EggLocationType.valueOf(config.getString("state.type", "UNKNOWN"));
            } catch (IllegalArgumentException e) {
                this.type = EggLocationType.UNKNOWN;
            }
        }
        
        if (config.contains("state.player-uuid")) {
            try {
                this.playerUUID = UUID.fromString(config.getString("state.player-uuid"));
            } catch (IllegalArgumentException e) {
                this.playerUUID = null;
            }
        }
        
        this.playerName = config.getString("state.player-name", "Unknown");
        this.world = config.getString("state.world", "");
        this.x = config.getInt("state.x", 0);
        this.y = config.getInt("state.y", 0);
        this.z = config.getInt("state.z", 0);
        this.since = config.getLong("state.since", 0);
    }

    public void save() {
        if (!plugin.mainConfig.persistent) {
            return;
        }

        if (config == null) {
            config = new YamlConfiguration();
        }

        config.set("state.type", type.name());
        config.set("state.player-uuid", playerUUID != null ? playerUUID.toString() : null);
        config.set("state.player-name", playerName);
        config.set("state.world", world);
        config.set("state.x", x);
        config.set("state.y", y);
        config.set("state.z", z);
        config.set("state.since", since);

        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save egg-state.yml!");
            e.printStackTrace();
        }
    }
}
