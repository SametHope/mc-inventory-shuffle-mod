package samethope.inventory_shuffle.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import samethope.inventory_shuffle.data.ModState;
import samethope.inventory_shuffle.data.ShuffleGroup;
import samethope.inventory_shuffle.data.ShuffleOptions;
import samethope.inventory_shuffle.utils.TextUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

/**
 * Handles all group-related commands for the inventory shuffle mod
 */
public class GroupCommands {
  public static int executeGroupCreateCommand(CommandContext<ServerCommandSource> context, String groupName) {
    if (groupName == null || groupName.trim().isEmpty()) {
      context.getSource().sendError(Text.literal("Group name cannot be empty."));
      return 0;
    }

    if (ModState.createGroup(groupName)) {
      MutableText text = TextUtils.getColoredText("Created group ", Formatting.GOLD);
      text.append(TextUtils.getColoredText("'" + groupName + "'", Formatting.YELLOW));
      context.getSource().sendFeedback(() -> text, true);
      return 1;
    } else {
      context.getSource()
          .sendError(Text.literal("A group named '" + groupName + "' already exists."));
      return 0;
    }
  }

  public static int executeGroupDeleteCommand(CommandContext<ServerCommandSource> context, String groupName) {
    if (groupName == null || groupName.trim().isEmpty()) {
      context.getSource().sendError(Text.literal("Group name cannot be empty."));
      return 0;
    }

    if (ModState.deleteGroup(groupName)) {
      MutableText text = TextUtils.getColoredText("Deleted group ", Formatting.GOLD);
      text.append(TextUtils.getColoredText("'" + groupName + "'", Formatting.YELLOW));
      context.getSource().sendFeedback(() -> text, true);
      return 1;
    } else {
      context.getSource().sendError(Text.literal("No group named '" + groupName + "' exists."));
      return 0;
    }
  }

  public static int executeGroupRenameCommand(CommandContext<ServerCommandSource> context, String oldGroupName,
      String newGroupName) {
    if (oldGroupName == null || oldGroupName.trim().isEmpty() || newGroupName == null
        || newGroupName.trim().isEmpty()) {
      context.getSource().sendError(Text.literal("Group names cannot be empty."));
      return 0;
    }

    if (ModState.renameGroup(oldGroupName, newGroupName)) {
      MutableText text = TextUtils.getColoredText("Renamed group ", Formatting.GOLD);
      text.append(TextUtils.getColoredText("'" + oldGroupName + "'", Formatting.YELLOW));
      text.append(TextUtils.getColoredText(" to ", Formatting.GOLD));
      text.append(TextUtils.getColoredText("'" + newGroupName + "'", Formatting.YELLOW));
      context.getSource().sendFeedback(() -> text, true);
      return 1;
    } else {
      if (ModState.getGroup(oldGroupName).isEmpty()) {
        context.getSource()
            .sendError(Text.literal("No group named '" + oldGroupName + "' exists."));
      } else {
        context.getSource()
            .sendError(Text.literal("A group named '" + newGroupName + "' already exists."));
      }
      return 0;
    }
  }

  public static int executeGroupOptionSetCommand(CommandContext<ServerCommandSource> context, String groupName,
      String optionKey, Object value) {
    if (groupName == null || groupName.trim().isEmpty() || optionKey == null || optionKey.trim().isEmpty()) {
      context.getSource()
          .sendError(Text.literal("Group name and option name cannot be empty."));
      return 0;
    }

    Optional<ShuffleGroup> groupOpt = ModState.getGroup(groupName);
    if (groupOpt.isEmpty()) {
      context.getSource().sendError(Text.literal("No group named '" + groupName + "' exists."));
      return 0;
    }

    ShuffleOptions option = ShuffleOptions.fromKey(optionKey);
    if (option == null) {
      context.getSource().sendError(Text.literal("Invalid option '" + optionKey + "'."));
      return 0;
    }

    ShuffleGroup group = groupOpt.get();
    if (option.applyTo(group, value)) {
      MutableText text = TextUtils.getColoredText("Set option ", Formatting.GOLD);
      text.append(TextUtils.getColoredText("'" + optionKey + "'", Formatting.YELLOW));
      text.append(TextUtils.getColoredText(" to ", Formatting.GOLD));

      if (value instanceof Boolean booleanValue) {
        text.append(TextUtils.getColoredText(
            booleanValue ? "Enabled" : "Disabled",
            booleanValue ? Formatting.GREEN : Formatting.RED));
      } else {
        text.append(TextUtils.getColoredText(value.toString(), Formatting.YELLOW));
      }

      text.append(TextUtils.getColoredText(" for group ", Formatting.GOLD));
      text.append(TextUtils.getColoredText("'" + groupName + "'", Formatting.YELLOW));

      context.getSource().sendFeedback(() -> text, true);
      return 1;
    } else {
      context.getSource()
          .sendError(Text.literal("Invalid value type for option '" + optionKey + "'."));
      return 0;
    }
  }

  /**
   * Shows the current value of a specific option for a group
   */
  public static int executeGroupShowOptionCommand(CommandContext<ServerCommandSource> context, String groupName,
      String optionKey) {
    if (groupName == null || groupName.trim().isEmpty() || optionKey == null || optionKey.trim().isEmpty()) {
      context.getSource()
          .sendError(Text.literal("Group name and option name cannot be empty."));
      return 0;
    }

    Optional<ShuffleGroup> groupOpt = ModState.getGroup(groupName);
    if (groupOpt.isEmpty()) {
      context.getSource().sendError(Text.literal("No group named '" + groupName + "' exists."));
      return 0;
    }

    ShuffleOptions option = ShuffleOptions.fromKey(optionKey);
    if (option == null) {
      context.getSource().sendError(Text.literal("Invalid option '" + optionKey + "'."));
      return 0;
    }

    ShuffleGroup group = groupOpt.get();
    Object value = switch (optionKey) {
      case "enabled" -> group.isEnabled();
      case "interval" -> group.getInterval();
      case "shuffleEmptySlots" -> group.isShuffleEmptySlots();
      case "shuffleInventory" -> group.isShuffleInventory();
      case "shuffleHotbar" -> group.isShuffleHotbar();
      case "shuffleHand" -> group.isShuffleHand();
      case "shuffleOffhand" -> group.isShuffleOffhand();
      default -> null;
    };

    if (value != null) {
      MutableText text = TextUtils.getColoredText("Option ", Formatting.GOLD);
      text.append(TextUtils.getColoredText("'" + optionKey + "'", Formatting.YELLOW));
      text.append(TextUtils.getColoredText(" for group ", Formatting.GOLD));
      text.append(TextUtils.getColoredText("'" + groupName + "'", Formatting.YELLOW));
      text.append(TextUtils.getColoredText(" is: ", Formatting.GOLD));

      if (value instanceof Boolean booleanValue) {
        text.append(TextUtils.getColoredText(
            booleanValue ? "Enabled" : "Disabled",
            booleanValue ? Formatting.GREEN : Formatting.RED));
      } else {
        text.append(TextUtils.getColoredText(value.toString(), Formatting.YELLOW));
      }
      context.getSource().sendFeedback(() -> text, false);
      return 1;
    } else {
      context.getSource().sendError(Text.literal("Could not retrieve value for option '" + optionKey + "'."));
      return 0;
    }
  }

  /**
   * Shows all options for a group without showing player information
   */
  public static int executeGroupShowAllOptionsCommand(CommandContext<ServerCommandSource> context, String groupName) {
    if (groupName == null || groupName.trim().isEmpty()) {
      context.getSource().sendError(Text.literal("Group name cannot be empty."));
      return 0;
    }

    Optional<ShuffleGroup> groupOpt = ModState.getGroup(groupName);
    if (groupOpt.isEmpty()) {
      context.getSource().sendError(Text.literal("No group named '" + groupName + "' exists."));
      return 0;
    }

    ShuffleGroup group = groupOpt.get();
    List<UUID> playerUuids = ModState.getPlayersInGroup(groupName);

    MutableText content = Text.literal("");

    // Add heading with enabled, interval and player count
    content.append(TextUtils.getColoredText("Group ", Formatting.GOLD));
    content.append(TextUtils.getColoredText("'" + groupName + "'", Formatting.YELLOW));
    content.append(TextUtils.getColoredText(" (", Formatting.GRAY));
    content.append(TextUtils.getColoredText(
        group.isEnabled() ? "Enabled" : "Disabled",
        group.isEnabled() ? Formatting.GREEN : Formatting.RED));
    content.append(TextUtils.getColoredText(", Interval: ", Formatting.GRAY));
    content.append(TextUtils.getColoredText(String.valueOf(group.getInterval()), Formatting.YELLOW));
    content.append(TextUtils.getColoredText(" ticks, Players: ", Formatting.GRAY));
    content.append(TextUtils.getColoredText(String.valueOf(playerUuids.size()), Formatting.YELLOW));
    content.append(TextUtils.getColoredText(")", Formatting.GRAY));

    // Add shuffle options
    content.append(Text.literal("\n"));
    content.append(TextUtils.getColoredText("  shuffleEmptySlots: ", Formatting.GRAY));
    content.append(TextUtils.getColoredText(
        group.isShuffleEmptySlots() ? "Enabled" : "Disabled",
        group.isShuffleEmptySlots() ? Formatting.GREEN : Formatting.RED));
    content.append(Text.literal("\n"));
    content.append(TextUtils.getColoredText("  shuffleInventory: ", Formatting.GRAY));
    content.append(TextUtils.getColoredText(
        group.isShuffleInventory() ? "Enabled" : "Disabled",
        group.isShuffleInventory() ? Formatting.GREEN : Formatting.RED));
    content.append(Text.literal("\n"));
    content.append(TextUtils.getColoredText("  shuffleHotbar: ", Formatting.GRAY));
    content.append(TextUtils.getColoredText(
        group.isShuffleHotbar() ? "Enabled" : "Disabled",
        group.isShuffleHotbar() ? Formatting.GREEN : Formatting.RED));
    content.append(Text.literal("\n"));
    content.append(TextUtils.getColoredText("  shuffleHand: ", Formatting.GRAY));
    content.append(TextUtils.getColoredText(
        group.isShuffleHand() ? "Enabled" : "Disabled",
        group.isShuffleHand() ? Formatting.GREEN : Formatting.RED));
    content.append(Text.literal("\n"));
    content.append(TextUtils.getColoredText("  shuffleOffhand: ", Formatting.GRAY));
    content.append(TextUtils.getColoredText(
        group.isShuffleOffhand() ? "Enabled" : "Disabled",
        group.isShuffleOffhand() ? Formatting.GREEN : Formatting.RED));

    context.getSource().sendFeedback(() -> content, false);
    return 1;
  }
}