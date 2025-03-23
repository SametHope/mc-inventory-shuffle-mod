package samethope.inventory_shuffle.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import samethope.inventory_shuffle.data.ModState;
import samethope.inventory_shuffle.utils.TextUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles all player-related commands for the inventory shuffle mod
 */
public class PlayerCommands {

  /**
   * Adds one or more players to a specified group
   */
  public static int executePlayerAddCommand(CommandContext<ServerCommandSource> context, String groupName,
      Collection<ServerPlayerEntity> players) {
    if (groupName == null || groupName.trim().isEmpty()) {
      context.getSource().sendError(Text.literal("Group name cannot be empty."));
      return 0;
    }

    if (players == null || players.isEmpty()) {
      context.getSource().sendError(Text.literal("No players specified."));
      return 0;
    }

    if (ModState.getGroup(groupName).isEmpty()) {
      context.getSource().sendError(Text.literal("No group named '" + groupName + "' exists."));
      return 0;
    }

    AtomicInteger count = new AtomicInteger(0);
    for (ServerPlayerEntity player : players) {
      if (player == null) {
        continue;
      }

      Optional<String> currentGroup = ModState.getPlayerGroupName(player);
      if (currentGroup.isPresent() && currentGroup.get().equals(groupName)) {
        context.getSource()
            .sendError(Text.literal(player.getName().getString() + " is already in group '" + groupName + "'."));
        continue;
      }

      if (ModState.addPlayerToGroup(player, groupName)) {
        count.incrementAndGet();
      }
    }

    if (count.get() > 0) {
      if (count.get() == 1) {
        MutableText text = TextUtils.getColoredText("Added ", Formatting.GOLD);
        text.append(TextUtils.getColoredText("1 player", Formatting.YELLOW));
        text.append(TextUtils.getColoredText(" to group ", Formatting.GOLD));
        text.append(TextUtils.getColoredText("'" + groupName + "'", Formatting.YELLOW));
        context.getSource().sendFeedback(() -> text, true);
      } else {
        MutableText text = TextUtils.getColoredText("Added ", Formatting.GOLD);
        text.append(TextUtils.getColoredText(count.get() + " players", Formatting.YELLOW));
        text.append(TextUtils.getColoredText(" to group ", Formatting.GOLD));
        text.append(TextUtils.getColoredText("'" + groupName + "'", Formatting.YELLOW));
        context.getSource().sendFeedback(() -> text, true);
      }
      return count.get();
    } else {
      context.getSource()
          .sendFeedback(() -> TextUtils.getColoredText("No players were added to the group.", Formatting.GRAY), false);
      return 0;
    }
  }

  /**
   * Adds the command issuer to a specified group
   */
  public static int executePlayerAddSelfCommand(CommandContext<ServerCommandSource> context, String groupName) {
    if (groupName == null || groupName.trim().isEmpty()) {
      context.getSource().sendError(Text.literal("Group name cannot be empty."));
      return 0;
    }

    if (ModState.getGroup(groupName).isEmpty()) {
      context.getSource().sendError(Text.literal("No group named '" + groupName + "' exists."));
      return 0;
    }

    ServerCommandSource source = context.getSource();
    if (!source.isExecutedByPlayer()) {
      source.sendError(Text.literal("This command can only be used by a player when no target players are specified."));
      return 0;
    }

    ServerPlayerEntity player = source.getPlayer();
    if (player == null) {
      return 0;
    }

    Optional<String> currentGroup = ModState.getPlayerGroupName(player);
    if (currentGroup.isPresent() && currentGroup.get().equals(groupName)) {
      source.sendError(Text.literal("You are already in group '" + groupName + "'."));
      return 0;
    }

    if (ModState.addPlayerToGroup(player, groupName)) {
      MutableText text = TextUtils.getColoredText("Added ", Formatting.GOLD);
      text.append(TextUtils.getColoredText("you", Formatting.YELLOW));
      text.append(TextUtils.getColoredText(" to group ", Formatting.GOLD));
      text.append(TextUtils.getColoredText("'" + groupName + "'", Formatting.YELLOW));
      source.sendFeedback(() -> text, true);
      return 1;
    } else {
      source.sendFeedback(() -> TextUtils.getColoredText("Could not add you to the group.", Formatting.RED), false);
      return 0;
    }
  }

  /**
   * Removes a player from their group by name
   */
  public static int executePlayerRemoveByNameCommand(CommandContext<ServerCommandSource> context, String playerName) {
    if (playerName == null || playerName.trim().isEmpty()) {
      context.getSource().sendError(Text.literal("Player name cannot be empty."));
      return 0;
    }

    MinecraftServer server = context.getSource().getServer();
    String groupName = null;
    boolean removed = false;

    // First check if this is an online player
    ServerPlayerEntity onlinePlayer = null;
    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
      if (player != null && player.getName().getString().equals(playerName)) {
        onlinePlayer = player;
        break;
      }
    }

    if (onlinePlayer != null) {
      // Online player found
      Optional<String> currentGroup = ModState.getPlayerGroupName(onlinePlayer);
      if (currentGroup.isPresent()) {
        groupName = currentGroup.get();
        removed = ModState.removePlayer(onlinePlayer);
      } else {
        context.getSource().sendError(Text.literal("Player '" + playerName + "' is not in any group."));
        return 0;
      }
    } else {
      // Try offline players
      for (UUID uuid : ModState.getGroupedPlayerUuids()) {
        if (uuid == null) {
          continue;
        }

        String storedName = ModState.getPlayerName(uuid);
        if (storedName != null && storedName.equalsIgnoreCase(playerName)) {
          Optional<String> currentGroup = ModState.getPlayerGroupName(uuid);
          if (currentGroup.isPresent()) {
            groupName = currentGroup.get();
            removed = ModState.removePlayerByUuid(uuid);
          }
          break;
        }
      }
    }

    if (removed) {
      MutableText text = TextUtils.getColoredText("Removed player ", Formatting.GOLD);
      text.append(TextUtils.getColoredText("'" + playerName + "'", Formatting.YELLOW));
      text.append(TextUtils.getColoredText(" from group ", Formatting.GOLD));
      text.append(TextUtils.getColoredText("'" + groupName + "'", Formatting.YELLOW));
      context.getSource().sendFeedback(() -> text, true);
      return 1;
    } else {
      context.getSource().sendError(Text.literal("Could not find player '" + playerName + "' in any group."));
      return 0;
    }
  }

  /**
   * Removes the command issuer from their group
   */
  public static int executePlayerRemoveSelfCommand(CommandContext<ServerCommandSource> context) {
    ServerCommandSource source = context.getSource();
    if (!source.isExecutedByPlayer()) {
      source.sendError(Text.literal("This command can only be used by a player when no target player is specified."));
      return 0;
    }

    ServerPlayerEntity player = source.getPlayer();
    if (player == null) {
      return 0;
    }

    Optional<String> currentGroup = ModState.getPlayerGroupName(player);
    if (currentGroup.isEmpty()) {
      source.sendError(Text.literal("You are not in any group."));
      return 0;
    }

    String groupName = currentGroup.get();
    if (ModState.removePlayer(player)) {
      MutableText text = TextUtils.getColoredText("Removed ", Formatting.GOLD);
      text.append(TextUtils.getColoredText("you", Formatting.YELLOW));
      text.append(TextUtils.getColoredText(" from group ", Formatting.GOLD));
      text.append(TextUtils.getColoredText("'" + groupName + "'", Formatting.YELLOW));
      source.sendFeedback(() -> text, true);
      return 1;
    } else {
      source.sendFeedback(() -> TextUtils.getColoredText("Could not remove you from your group.", Formatting.RED),
          false);
      return 0;
    }
  }
}