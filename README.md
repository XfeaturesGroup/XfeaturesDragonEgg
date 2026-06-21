# 🥚 XfeaturesDragonEgg

![Version](https://img.shields.io/badge/version-1.0.0--SNAPSHOT-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.21%2B-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-25-orange.svg)
![License](https://img.shields.io/badge/license-MIT-yellow.svg)
![Discord](https://img.shields.io/discord/1252242781775335505?color=7289da&label=discord&logo=discord&logoColor=white)

## 📝 Description

**XfeaturesDragonEgg** is a comprehensive utility plugin for Minecraft servers that introduces advanced tracking, protection, and visual enhancements for the highly coveted Dragon Egg. No more lost or stolen eggs!

- 🛡️ **Container protection**: Prevent players from hiding the Dragon Egg in Ender Chests, Shulker Boxes, Bundles, Dispensers, Furnaces, and more. It can only be stored in standard Chests or Trapped Chests.
- 📍 **Advanced tracking**: Always know where the Dragon Egg is! Track it in player inventories, chests, placed blocks, or even if it's dropped on the ground.
- ✨ **Visual effects**: Players carrying the Dragon Egg emit a unique pink glowing effect and beautiful End Rod particles.
- 🛡️ **Damage immunity**: The Dragon Egg (as a dropped item) is completely immune to fire, lava, explosions, cactus damage, and it will never despawn!

## ✨ Key features

- **Container restriction**: Fully configurable list of blocked container types.
- **Persistent tracking**: The egg's location and state are saved even after a server restart.
- **Dynamic visual effects**: Unique aesthetics for the player possessing the Dragon Egg.
- **Dropped item protection**: Comprehensive anti-damage and anti-despawn mechanics for the Dragon Egg item.
- **Full message customization**: All notifications and interface text can be changed in the configuration.
- **Easy management**: Intuitive commands for administrators to track the egg and reload configurations.

## 📋 Requirements

- Spigot/Paper 1.21+
- Java 25

## 🔧 Installation

1. Download the latest version of the plugin from the [Releases](https://github.com/XfeaturesGroup/XfeaturesDragonEgg/releases) section
2. Place the JAR file in the `plugins` folder of your server
3. Restart the server
4. Configure the configuration files as you see fit
5. Reload the plugin with the command `/dragonegg reload`

<details>
<summary>

## 🌍 Supported Languages

</summary>

XfeaturesDragonEgg supports multiple languages for users from around the world. You can change the plugin language using the command `/dragonegg language <language>`.

### Available Languages:

- English (en)
- Russian (ru)
- German (de)
- Polish (pl)
- Italian (it)
- Portuguese (pt)
- Turkish (tr)
- French (fr)
- Spanish (es)
- Lithuanian (lt)
- Latvian (lv)
- Estonian (et)
- Arabic (ar)
- Hindi (hi)
- Chinese (Simplified) (cn)
- Japanese (ja)
- Korean (ko)

### Adding a New Language

If you want to add a new language or improve an existing translation:

1. Copy the `messages-en.yml` file from the `resources/messages/` folder
2. Rename it to `messages-XX.yml`, where XX is your language code
3. Translate all strings to your language
4. Submit the translation via Pull Request or contact the developers

We always welcome new translations and improvements to existing ones!
</details>

<details>
<summary>

## ⚙️ Configuration

</summary>

After the first launch, the plugin will create the configuration file:

### config.yml
Basic plugin settings:

```yaml
# All available languages can be found here:
# https://github.com/XfeaturesGroup/XfeaturesDragonEgg/tree/master/examples/messages
# Language settings for the plugin
language: "en"

# Container protection
protection:
  enabled: true
  # List of blocked container types
  # The egg can ONLY be placed in a normal CHEST or TRAPPED_CHEST
  blocked-containers:
    - ENDER_CHEST
    - SHULKER_BOX
    - BUNDLE
    - DISPENSER
    - DROPPER
    - FURNACE
    - BLAST_FURNACE
    - SMOKER
    - BREWING_STAND
    - HOPPER
    - BARREL
    - CRAFTER

# Visual effects when carrying the egg
effects:
  enabled: true
  # Player glowing effect (pink/purple color)
  glowing:
    enabled: true
    color: "LIGHT_PURPLE"
  # END_ROD particles around the player
  particles:
    enabled: true
    type: "END_ROD"
    count: 3
    interval: 10

# Egg tracking
tracking:
  enabled: true
  persistent: true
```
</details>

## 📜 Commands
| Command | Description | Permissions |
|---------|----------|-------|
| `/dragonegg reload` | Reload configuration | `xfeatures.dragonegg.reload` |
| `/dragonegg where` | Show the Dragon Egg location | `xfeatures.dragonegg.where` |
| `/dragonegg help` | Show the help message | `xfeatures.dragonegg.use` |
| `/dragonegg language <lang>` | Change plugin language | `xfeatures.dragonegg.language` |

## 🔒 Permissions
- `xfeatures.dragonegg.use` - Access to the main DragonEgg command
- `xfeatures.dragonegg.where` - Access to track the Dragon Egg location
- `xfeatures.dragonegg.reload` - Access to the reload command
- `xfeatures.dragonegg.language` - Access to change the plugin language
- `xfeatures.dragonegg.bypass` - Bypass container restrictions for the Dragon Egg
- `xfeatures.dragonegg.effects` - Receive visual effects when carrying the egg

## 📊 Performance
### The plugin is optimized for minimal impact on server performance:
- Asynchronous data saving
- Efficient event handling
- Minimal memory usage

## 📝 Future plans
- ✅ Advanced container restrictions **(Implemented)**
- ✅ Support for more languages **(Implemented)**
- ✅ Anti-despawn and Anti-damage mechanics **(Implemented)**
- GUI for managing the Dragon Egg state

## 🤝 Contributing to the project
### Contributions are welcome! If you have ideas for improving the plugin:
1. Fork the repository
2. Create a branch for your feature (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push the changes to your fork (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📊 Statistics
### The plugin collects anonymous statistics via bStats to improve functionality:
You can view the plugin statistics on the [bStats](https://bstats.org/plugin/bukkit/XfeaturesDragonEgg/32137) page.

![bStats](https://img.shields.io/bstats/servers/32137?label=servers)
![bStats](https://img.shields.io/bstats/players/32137?label=players)

### Data collected:
- Protection features usage (Enabled/Disabled)
- Visual effects usage
- Tracking usage and persistence mode
- Number of players and servers
- Online mode status
- Server, plugin, and Java versions

### Disabling statistics
If you want to disable statistics collection, you can do so in the `plugins/bStats/config.yml` file by setting `enabled: false`. Disabling statistics does not affect the functionality of the plugin.

## 📞 Support
### If you encounter any problems or have any questions:
- Create an issue in the repository
- Contact us via [Discord](https://discord.gg/KJU4DjGkeH)

![Downloads](https://img.shields.io/github/downloads/XfeaturesGroup/XfeaturesDragonEgg/total?color=orange)
![Issues](https://img.shields.io/github/issues/XfeaturesGroup/XfeaturesDragonEgg?color=red)
![Last Commit](https://img.shields.io/github/last-commit/XfeaturesGroup/XfeaturesDragonEgg?color=blueviolet)

<p align="center">
  Made by the XfeaturesGroup Dev-team
</p>
