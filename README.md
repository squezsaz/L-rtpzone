# L-rtpzone Plugin

A Minecraft Spigot plugin that allows administrators to create mass teleportation zones where players are teleported to random locations in the world every 45 seconds.

## Features

- Admins can designate specific areas as mass teleportation zones
- Players within these zones are teleported together to random world locations every 45 seconds
- Players receive an explosion sound effect and "SAVAŞ!" title when teleported
- Configurable teleportation world and coordinate limits
- Multi-language support (English and Turkish)
- PlaceholderAPI support for customizing messages
- Easy zone management with intuitive commands
- Admins can view detailed zone information including player counts and teleport timers
- Configuration and language files can be reloaded without restarting the server
- **NEW:** Clickable zone names in info command for easy admin teleportation

## Commands

- `/mtz create <name>` - Create a new teleport zone (requires pos1 and pos2 to be set first)
- `/mtz remove <name>` - Remove an existing teleport zone
- `/mtz list` - List all active teleport zones
- `/mtz pos1` - Set the first corner of a zone selection
- `/mtz pos2` - Set the second corner of a zone selection
- `/mtz info` - View detailed information about all zones (Admin only)
- `/mtz teleport <zone>` - Teleport to a zone (Admin only)
- `/mtz reload` - Reload plugin configuration and language files (Admin only)

## Permissions

- `l-rtpzone.admin` - Allows access to all plugin commands

## Placeholders

- `%lrtpzone_zone_count%` - Number of active teleport zones
- `%lrtpzone_nearest_zone%` - Name of the nearest teleport zone
- `%lrtpzone_nearest_zone_time%` - Time until next teleport in the nearest zone
- `%lrtpzone_zone_<name>_players%` - Number of players in a specific zone
- `%lrtpzone_zone_<name>_countdown%` - Time until next teleport in a specific zone

## Installation

1. Download the latest release
2. Place the `.jar` file in your server's `plugins` folder
3. Restart the server
4. Ensure PlaceholderAPI is installed if you want to use placeholders

## Configuration

The plugin creates a `config.yml` file on first run with the following options:

```yaml
# Teleport interval in seconds
teleport-interval: 45

# Teleportation world and coordinate limits
# Players will be teleported to this world within the specified coordinate bounds
teleport-world:
  # Name of the world to teleport players to (leave empty to use the same world as the zone)
  world-name: ""
  # Coordinate limits for teleportation
  min-x: -1000
  max-x: 1000
  min-z: -1000
  max-z: 1000

# Language settings
# Available languages: en (English), tr (Turkish)
language: en

# Permission settings
permissions:
  admin: "l-rtpzone.admin"
```

## Language Support

The plugin supports multiple languages. You can change the language by modifying the `language` setting in the config.yml file:

- `en` - English (default)
- `tr` - Turkish

Language files are located in the `lang` folder and can be customized as needed.

## Reloading Configuration

To reload the plugin configuration and language files without restarting the server, use the command:
```
/mtz reload
```

This command requires the `l-rtpzone.admin` permission.

## New Feature: Clickable Zone Names

In the `/mtz info` command output, zone names are now clickable. When you click on a zone name, it will automatically teleport you to that zone's center. This makes it much easier for admins to navigate between zones.

## Building from Source

### Prerequisites
- Java JDK 17 or higher (OpenJDK or Oracle JDK)
- Apache Maven 3.6.0 or higher

### Building
1. Open a terminal/command prompt in the project directory
2. Run `mvn clean package`
3. The compiled JAR will be located in the `target` folder

Alternatively, you can run the `build.bat` file on Windows.

## Server Requirements
- Minecraft 1.21.1 server
- Java 17 or higher to run the server

## Support

discord: squezsaz
