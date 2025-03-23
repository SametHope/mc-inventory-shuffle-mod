package samethope.inventory_shuffle.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import samethope.inventory_shuffle.InventoryShuffle;
import samethope.inventory_shuffle.config.InventoryShuffleConfig;
import samethope.inventory_shuffle.services.InventoryShuffler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Singleton that manages the state of the inventory shuffle mod
 */
public class ModState {
  private static final String STATE_KEY = InventoryShuffle.MOD_ID;
  private static final Map<String, ShuffleGroup> groups = new HashMap<>();
  private static final Map<UUID, String> playerGroups = new HashMap<>();
  private static final Map<UUID, String> playerNames = new HashMap<>();

  /**
   * Saves the current mod state to an NBT file
   */
  public static void saveToNbt(MinecraftServer server) {
    try {
      NbtCompound rootNbt = new NbtCompound();

      NbtCompound groupsNbt = new NbtCompound();
      for (Map.Entry<String, ShuffleGroup> entry : groups.entrySet()) {
        groupsNbt.put(entry.getKey(), entry.getValue().toNbt());
      }
      rootNbt.put("Groups", groupsNbt);

      NbtCompound playerGroupsNbt = new NbtCompound();
      for (Map.Entry<UUID, String> entry : playerGroups.entrySet()) {
        playerGroupsNbt.putString(entry.getKey().toString(), entry.getValue());
      }
      rootNbt.put("PlayerGroups", playerGroupsNbt);

      NbtCompound playerNamesNbt = new NbtCompound();
      for (Map.Entry<UUID, String> entry : playerNames.entrySet()) {
        playerNamesNbt.putString(entry.getKey().toString(), entry.getValue());
      }
      rootNbt.put("PlayerNames", playerNamesNbt);

      Path filePath = Path.of(server.getSavePath(WorldSavePath.ROOT).toString(),
          STATE_KEY + InventoryShuffleConfig.SAVE_FILE_EXTENSION);
      NbtIo.write(rootNbt, filePath);

      if (InventoryShuffleConfig.DEBUG_LOG_ENABLED) {
        InventoryShuffle.LOGGER.debug("Saved mod state to {}", filePath);
      }
    } catch (IOException e) {
      InventoryShuffle.LOGGER.error("Failed to save mod state: {}", e.getMessage(), e);
    }
  }

  /**
   * Loads the mod state from an NBT file
   */
  public static void loadFromNbt(MinecraftServer server) {
    try {
      Path filePath = Path.of(server.getSavePath(WorldSavePath.ROOT).toString(),
          STATE_KEY + InventoryShuffleConfig.SAVE_FILE_EXTENSION);
      if (Files.exists(filePath)) {
        NbtCompound rootNbt = NbtIo.read(filePath);
        if (rootNbt != null) {
          groups.clear();
          playerGroups.clear();
          playerNames.clear();

          NbtCompound groupsNbt = rootNbt.getCompound("Groups");
          for (String groupName : groupsNbt.getKeys()) {
            NbtCompound groupNbt = groupsNbt.getCompound(groupName);
            groups.put(groupName, new ShuffleGroup(groupName, groupNbt));
          }

          NbtCompound playerGroupsNbt = rootNbt.getCompound("PlayerGroups");
          for (String uuidStr : playerGroupsNbt.getKeys()) {
            UUID uuid = UUID.fromString(uuidStr);
            String groupName = playerGroupsNbt.getString(uuidStr);
            if (groups.containsKey(groupName)) {
              playerGroups.put(uuid, groupName);
            }
          }

          if (rootNbt.contains("PlayerNames")) {
            NbtCompound playerNamesNbt = rootNbt.getCompound("PlayerNames");
            for (String uuidStr : playerNamesNbt.getKeys()) {
              UUID uuid = UUID.fromString(uuidStr);
              if (playerGroups.containsKey(uuid)) {
                String playerName = playerNamesNbt.getString(uuidStr);
                playerNames.put(uuid, playerName);
              }
            }
          }

          if (InventoryShuffleConfig.DEBUG_LOG_ENABLED) {
            InventoryShuffle.LOGGER.debug("Loaded mod state from {}", filePath);
          }
        } else {
          InventoryShuffle.LOGGER.debug("No valid saved state found at {}", filePath);
          createDefaultGroup();
        }
      } else {
        createDefaultGroup();
        if (InventoryShuffleConfig.DEBUG_LOG_ENABLED) {
          InventoryShuffle.LOGGER.debug("No saved state found at {}, using defaults", filePath);
        }
      }
    } catch (IOException e) {
      InventoryShuffle.LOGGER.error("Failed to load mod state: {}", e.getMessage(), e);
      createDefaultGroup();
    }
  }

  /**
   * Creates a default group if no groups exist
   */
  private static void createDefaultGroup() {
    groups.clear();
    playerGroups.clear();
    playerNames.clear();
    createGroup(InventoryShuffleConfig.DEFAULT_GROUP_NAME);
  }

  /**
   * Creates a new group with the given name
   */
  public static boolean createGroup(String name) {
    if (groups.containsKey(name)) {
      return false;
    }
    groups.put(name, new ShuffleGroup(name));
    return true;
  }

  /**
   * Deletes a group and removes all players from it
   */
  public static boolean deleteGroup(String name) {
    if (!groups.containsKey(name)) {
      return false;
    }

    groups.remove(name);
    playerGroups.entrySet().removeIf(entry -> entry.getValue().equals(name));

    return true;
  }

  /**
   * Lists all existing groups
   */
  public static List<String> listGroups() {
    List<String> groupNames = new ArrayList<>(groups.keySet());
    Collections.sort(groupNames);
    return groupNames;
  }

  /**
   * Gets a group by name
   */
  public static Optional<ShuffleGroup> getGroup(String name) {
    return Optional.ofNullable(groups.get(name));
  }

  /**
   * Adds a player to a group
   */
  public static boolean addPlayerToGroup(ServerPlayerEntity player, String groupName) {
    if (!groups.containsKey(groupName)) {
      return false;
    }

    playerGroups.put(player.getUuid(), groupName);
    playerNames.put(player.getUuid(), player.getName().getString());
    return true;
  }

  /**
   * Removes a player from their group
   */
  public static boolean removePlayer(ServerPlayerEntity player) {
    boolean removed = playerGroups.remove(player.getUuid()) != null;
    if (removed) {
      playerNames.remove(player.getUuid());
    }
    return removed;
  }

  /**
   * Renames a group
   */
  public static boolean renameGroup(String oldName, String newName) {
    if (!groups.containsKey(oldName) || groups.containsKey(newName)) {
      return false;
    }

    ShuffleGroup group = groups.remove(oldName);

    ShuffleGroup newGroup = new ShuffleGroup(newName);
    newGroup.setEnabled(group.isEnabled());
    newGroup.setInterval(group.getInterval());
    newGroup.setShuffleEmptySlots(group.isShuffleEmptySlots());
    newGroup.setShuffleInventory(group.isShuffleInventory());
    newGroup.setShuffleHotbar(group.isShuffleHotbar());
    newGroup.setShuffleHand(group.isShuffleHand());
    newGroup.setShuffleOffhand(group.isShuffleOffhand());

    groups.put(newName, newGroup);

    for (Map.Entry<UUID, String> entry : playerGroups.entrySet()) {
      if (entry.getValue().equals(oldName)) {
        playerGroups.put(entry.getKey(), newName);
      }
    }

    return true;
  }

  /**
   * Gets a player's group
   */
  public static Optional<ShuffleGroup> getPlayerGroup(ServerPlayerEntity player) {
    String groupName = playerGroups.get(player.getUuid());
    if (groupName != null) {
      return Optional.ofNullable(groups.get(groupName));
    }
    return Optional.empty();
  }

  /**
   * Gets a player's group name
   */
  public static Optional<String> getPlayerGroupName(ServerPlayerEntity player) {
    String groupName = playerGroups.get(player.getUuid());
    if (groupName != null) {
      return Optional.of(groupName);
    }
    return Optional.empty();
  }

  /**
   * Gets a player's group name by UUID
   */
  public static Optional<String> getPlayerGroupName(UUID uuid) {
    String groupName = playerGroups.get(uuid);
    if (groupName != null) {
      return Optional.of(groupName);
    }
    return Optional.empty();
  }

  /**
   * Gets all players in a group
   */
  public static List<UUID> getPlayersInGroup(String groupName) {
    List<UUID> players = new ArrayList<>();

    for (Map.Entry<UUID, String> entry : playerGroups.entrySet()) {
      if (entry.getValue().equals(groupName)) {
        players.add(entry.getKey());
      }
    }

    return players;
  }

  /**
   * Processes a server tick, including updating shuffle counters and shuffling
   * inventories
   */
  public static void processTick(MinecraftServer server) {
    for (ShuffleGroup group : groups.values()) {
      group.tick();

      if (group.shouldShuffle()) {
        List<UUID> playersToShuffle = getPlayersInGroup(group.getName());
        if (!playersToShuffle.isEmpty() && InventoryShuffleConfig.DEBUG_LOG_ENABLED) {
          InventoryShuffle.LOGGER.debug("Shuffling inventories for group: {} ({} players)", group.getName(),
              playersToShuffle.size());
        }

        for (UUID uuid : playersToShuffle) {
          ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
          if (player != null) {
            InventoryShuffler.shuffleInventory(player);
          }
        }
      }
    }
  }

  /**
   * Gets a player's name by UUID
   */
  public static String getPlayerName(UUID uuid) {
    return playerNames.getOrDefault(uuid, null);
  }

  /**
   * Gets all UUIDs of players in any group
   */
  public static Set<UUID> getGroupedPlayerUuids() {
    return new HashSet<>(playerGroups.keySet());
  }

  /**
   * Removes a player from their group by UUID
   */
  public static boolean removePlayerByUuid(UUID uuid) {
    boolean removed = playerGroups.remove(uuid) != null;
    if (removed) {
      playerNames.remove(uuid);
    }
    return removed;
  }
}