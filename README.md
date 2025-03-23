# Minecraft Inventory Shuffle Mod

This is a Fabric mod that shuffles players' inventories based on groups and those groups' options. This mod supports **single-player**, **integrated/LAN servers**, and **dedicated servers**.

This mod shuffles inventories on a per-player basis; it does not shuffle/swap inventories between players.

### Official Pages
- https://github.com/SametHope/mc-inventory-shuffle-mod
- https://www.curseforge.com/minecraft/mc-mods/inventory-shuffle-mod
- https://modrinth.com/mod/inventory-shuffle-mod

## Overview
The mod organizes players into named groups, each with its own set of rules for inventory shuffling. When a player joins a group, their inventory will be shuffled according to that group's options. Each group can have its own shuffle interval and specific rules about which parts of the inventory to shuffle (main inventory, hotbar, selected slot, or offhand).

The mod provides commands to manage these groups and their options. You can create, delete, and rename groups, add or remove players from groups, and configure various shuffle options like intervals and which inventory sections to include in the shuffle. All group data and options are automatically saved with the world, so they persist between server restarts.

## Quick Start
A default group named "default" is created automatically when the mod first loads. To start shuffling:

1. Add players to a group: `/invshuffle player add <groupName> <player>`
2. Check group status: `/invshuffle group status <groupName>` (optional)
3. Adjust options if needed: `/invshuffle group options <groupName> [option] [value]` (optional)

## Commands
The mod is managed by commands. It adds a single command `/invshuffle` (or its alias `/shuffleinv`) from which all other configuration or information commands are derived. All commands require operator permissions (level 4).

> **Note:** In the command syntax below, parameters in `<angle brackets>` are required, while parameters in `[square brackets]` are optional.

| Command                                                  | Description                                                                                                                                                                                               |
| -------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Group Management**                                     |
| `/invshuffle group create <groupName>`                   | Creates a new group with default options. The group name must be unique.                                                                                                                                  |
| `/invshuffle group delete <groupName>`                   | Deletes an existing group and removes all players from it.                                                                                                                                                |
| `/invshuffle group rename <oldGroupName> <newGroupName>` | Renames an existing group to a new name. The new name must be unique.                                                                                                                                     |
| **Group Options**                                        |
| `/invshuffle group options <groupName> [option] [value]` | Shows all options for the group if no option is specified. Shows the value of a specific option if only option is specified. Sets an option to the specified value if both option and value are provided. |
| `/invshuffle group status [groupName]`                   | Shows a list of all groups if no group name is specified. Shows information about the specified group if a group name is provided.                                                                        |
| **Player Management**                                    |
| `/invshuffle player add <groupName> [players]`           | Adds yourself to the group if no players are specified. Adds the specified players to the group if players are provided. Players must be online to be added.                                              |
| `/invshuffle player remove [playerName]`                 | Removes yourself from your current group if no player name is specified. Removes the specified player from their group if a player name is provided. Works even if the player is offline.                 |
| **Utility Commands**                                     |
| `/invshuffle help`                                       | Displays all available commands and their basic usage, including a link to the mod's documentation (here).                                                                                                |

### Command Parameters
| Parameter        | Description                                                                                                           |
| ---------------- | --------------------------------------------------------------------------------------------------------------------- |
| `<groupName>`    | Name of the group                                                                                                     |
| `<oldGroupName>` | Name of the group to be renamed                                                                                       |
| `<newGroupName>` | New name for the group when renaming                                                                                  |
| `[playerName]`   | Name of the player to remove from their group                                                                         |
| `[players]`      | One or more players to add to a group                                                                                 |
| `[option]`       | Option to modify (enabled, interval, shuffleEmptySlots, shuffleInventory, shuffleHotbar, shuffleHand, shuffleOffhand) |
| `[value]`        | New value for the option (true/false for boolean options, number > 0 for interval)                                    |
| `[groupName]`    | Name of the group to show status for                                                                                  |

## FAQ

### Why isn't my inventory being shuffled?
1. **Not in a group** - Add yourself with `/invshuffle player add <groupName>`.
2. **Group disabled** - Enable with `/invshuffle group options <groupName> enabled true`.
3. **No shuffle options enabled** - At least one section (inventory, hotbar, etc.) must be enabled.
4. **Long interval** - If interval is high, shuffling occurs less frequently.
5. **Empty inventory** - No items means nothing to shuffle.

### Why aren't my hand/offhand items being shuffled?
`shuffleHand` and `shuffleOffhand` options have higher priority than `shuffleHotbar` option.
To include hands in the shuffle pool, make sure these options are enabled.  

Note that you can also shuffle only the hands and nothing else with the correct combination.

### Where is the mod data saved?
In the server's or save's root folder as `inventory-shuffle.dat` (NBT format, uncompressed).

### Why can't I add a player to a group?
Players must be online to be added (so we can get their UUID). You can remove offline players.

### Why can't I remove multiple players at once?
The remove command accepts only one name to support removing offline players by name.

### Is this mod compatible with other mods and datapacks?
Generally compatible with mods and datapacks that don't directly modify player inventories. Works with [SharedInv](https://github.com/red-stoned/sharedinv) for example but your experience may vary with other content.

## Notes
- A default group is only created when no save file exists.
- Play around with the group options, the defaults are arbitrary.