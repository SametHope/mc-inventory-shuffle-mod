package samethope.inventory_shuffle.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import samethope.inventory_shuffle.config.InventoryShuffleConfig;
import samethope.inventory_shuffle.data.ModState;
import samethope.inventory_shuffle.data.ShuffleGroup;
import samethope.inventory_shuffle.utils.TextUtils;

import java.util.*;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class InvShuffleCommand {

  @SuppressWarnings("unused")
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
      CommandManager.RegistrationEnvironment environment) {
    LiteralArgumentBuilder<ServerCommandSource> invShuffleCommand = literal(InventoryShuffleConfig.COMMAND_NAME)
        .requires(source -> source.hasPermissionLevel(InventoryShuffleConfig.COMMAND_PERMISSION_LEVEL))
        .executes(InvShuffleCommand::executeHelpCommand)
        .then(literal("group")
            .then(literal("create")
                .then(argument("groupName", StringArgumentType.word())
                    .executes(context -> GroupCommands.executeGroupCreateCommand(
                        context,
                        StringArgumentType.getString(context, "groupName")))))
            .then(literal("delete")
                .then(argument("groupName", StringArgumentType.word())
                    .suggests((context, builder) -> {
                      for (String group : ModState.listGroups()) {
                        builder.suggest(group);
                      }
                      return builder.buildFuture();
                    })
                    .executes(context -> GroupCommands.executeGroupDeleteCommand(
                        context,
                        StringArgumentType.getString(context, "groupName")))))
            .then(literal("rename")
                .then(argument("oldGroupName", StringArgumentType.word())
                    .suggests((context, builder) -> {
                      for (String group : ModState.listGroups()) {
                        builder.suggest(group);
                      }
                      return builder.buildFuture();
                    })
                    .then(argument("newGroupName", StringArgumentType.word())
                        .executes(context -> GroupCommands.executeGroupRenameCommand(
                            context,
                            StringArgumentType.getString(context, "oldGroupName"),
                            StringArgumentType.getString(context, "newGroupName"))))))
            .then(literal("options")
                .then(argument("groupName", StringArgumentType.word())
                    .suggests((context, builder) -> {
                      for (String group : ModState.listGroups()) {
                        builder.suggest(group);
                      }
                      return builder.buildFuture();
                    })
                    .executes(context -> GroupCommands.executeGroupShowAllOptionsCommand(
                        context,
                        StringArgumentType.getString(context, "groupName")))
                    .then(literal("enabled")
                        .executes(context -> GroupCommands.executeGroupShowOptionCommand(
                            context,
                            StringArgumentType.getString(context, "groupName"),
                            "enabled"))
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(context -> GroupCommands.executeGroupOptionSetCommand(
                                context,
                                StringArgumentType.getString(context, "groupName"),
                                "enabled",
                                BoolArgumentType.getBool(context, "value")))))
                    .then(literal("interval")
                        .executes(context -> GroupCommands.executeGroupShowOptionCommand(
                            context,
                            StringArgumentType.getString(context, "groupName"),
                            "interval"))
                        .then(argument("value", IntegerArgumentType.integer(1))
                            .executes(context -> GroupCommands.executeGroupOptionSetCommand(
                                context,
                                StringArgumentType.getString(context, "groupName"),
                                "interval",
                                IntegerArgumentType.getInteger(context, "value")))))
                    .then(literal("shuffleEmptySlots")
                        .executes(context -> GroupCommands.executeGroupShowOptionCommand(
                            context,
                            StringArgumentType.getString(context, "groupName"),
                            "shuffleEmptySlots"))
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(context -> GroupCommands.executeGroupOptionSetCommand(
                                context,
                                StringArgumentType.getString(context, "groupName"),
                                "shuffleEmptySlots",
                                BoolArgumentType.getBool(context, "value")))))
                    .then(literal("shuffleInventory")
                        .executes(context -> GroupCommands.executeGroupShowOptionCommand(
                            context,
                            StringArgumentType.getString(context, "groupName"),
                            "shuffleInventory"))
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(context -> GroupCommands.executeGroupOptionSetCommand(
                                context,
                                StringArgumentType.getString(context, "groupName"),
                                "shuffleInventory",
                                BoolArgumentType.getBool(context, "value")))))
                    .then(literal("shuffleHotbar")
                        .executes(context -> GroupCommands.executeGroupShowOptionCommand(
                            context,
                            StringArgumentType.getString(context, "groupName"),
                            "shuffleHotbar"))
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(context -> GroupCommands.executeGroupOptionSetCommand(
                                context,
                                StringArgumentType.getString(context, "groupName"),
                                "shuffleHotbar",
                                BoolArgumentType.getBool(context, "value")))))
                    .then(literal("shuffleHand")
                        .executes(context -> GroupCommands.executeGroupShowOptionCommand(
                            context,
                            StringArgumentType.getString(context, "groupName"),
                            "shuffleHand"))
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(context -> GroupCommands.executeGroupOptionSetCommand(
                                context,
                                StringArgumentType.getString(context, "groupName"),
                                "shuffleHand",
                                BoolArgumentType.getBool(context, "value")))))
                    .then(literal("shuffleOffhand")
                        .executes(context -> GroupCommands.executeGroupShowOptionCommand(
                            context,
                            StringArgumentType.getString(context, "groupName"),
                            "shuffleOffhand"))
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(context -> GroupCommands.executeGroupOptionSetCommand(
                                context,
                                StringArgumentType.getString(context, "groupName"),
                                "shuffleOffhand",
                                BoolArgumentType.getBool(context, "value")))))))
            .then(literal("status")
                .executes(InvShuffleCommand::executeStatusCommand)
                .then(argument("groupName", StringArgumentType.word())
                    .suggests((context, builder) -> {
                      for (String group : ModState.listGroups()) {
                        builder.suggest(group);
                      }
                      return builder.buildFuture();
                    })
                    .executes(context -> InvShuffleCommand.executeStatusCommand(
                        context,
                        StringArgumentType.getString(context, "groupName"))))))
        .then(literal("player")
            .then(literal("add")
                .then(argument("groupName", StringArgumentType.word())
                    .suggests((context, builder) -> {
                      for (String group : ModState.listGroups()) {
                        builder.suggest(group);
                      }
                      return builder.buildFuture();
                    })
                    .executes(context -> PlayerCommands.executePlayerAddSelfCommand(
                        context,
                        StringArgumentType.getString(context, "groupName")))
                    .then(argument("players", EntityArgumentType.players())
                        .executes(context -> PlayerCommands.executePlayerAddCommand(
                            context,
                            StringArgumentType.getString(context, "groupName"),
                            EntityArgumentType.getPlayers(context, "players"))))))
            .then(literal("remove")
                .executes(PlayerCommands::executePlayerRemoveSelfCommand)
                .then(argument("playerName", StringArgumentType.word())
                    .suggests((context, builder) -> {
                      for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager()
                          .getPlayerList()) {
                        if (ModState.getPlayerGroupName(player).isPresent()) {
                          builder.suggest(player.getName().getString());
                        }
                      }

                      for (UUID playerUuid : ModState.getGroupedPlayerUuids()) {
                        String name = ModState.getPlayerName(playerUuid);
                        if (name != null) {
                          builder.suggest(name);
                        }
                      }
                      return builder.buildFuture();
                    })
                    .executes(context -> PlayerCommands.executePlayerRemoveByNameCommand(
                        context,
                        StringArgumentType.getString(context, "playerName"))))))
        .then(literal("help")
            .executes(InvShuffleCommand::executeHelpCommand));

    dispatcher.register(invShuffleCommand);

    dispatcher.register(literal(InventoryShuffleConfig.COMMAND_ALIAS)
        .requires(source -> source.hasPermissionLevel(InventoryShuffleConfig.COMMAND_PERMISSION_LEVEL))
        .executes(InvShuffleCommand::executeHelpCommand)
        .redirect(dispatcher.getRoot().getChild(InventoryShuffleConfig.COMMAND_NAME)));
  }

  private static int executeHelpCommand(CommandContext<ServerCommandSource> context) {
    context.getSource().sendFeedback(() -> createHelpText(context.getSource().isExecutedByPlayer()), false);
    return 1;
  }

  private static Text createHelpText(boolean isPlayer) {
    MutableText title = TextUtils.getColoredText("Inventory Shuffle Commands\n", Formatting.GOLD);
    MutableText commands = TextUtils.getColoredText(
        """
            /invshuffle group create <groupName>
            /invshuffle group delete <groupName>
            /invshuffle group rename <oldGroupName> <newGroupName>
            /invshuffle group options <groupName> [<option> [<value>]]
            /invshuffle group status [groupName]
            /invshuffle player add <groupName> [players]
            /invshuffle player remove [playerName]
            /invshuffle help
            """,
        Formatting.YELLOW);

    MutableText githubText;
    if (isPlayer) {
      githubText = TextUtils.getClickableColoredUnderlinedText(
          "the github page of the mod",
          "https://github.com/SametHope/mc-inventory-shuffle-mod",
          Formatting.BLUE);
    } else {
      githubText = TextUtils.getColoredText(
          "https://github.com/SametHope/mc-inventory-shuffle-mod",
          Formatting.BLUE);
    }

    return title
        .append(commands)
        .append(TextUtils.getColoredText("For usage details, visit ", Formatting.GRAY))
        .append(githubText);
  }

  private static int executeStatusCommand(CommandContext<ServerCommandSource> context) {
    return executeStatusCommand(context, null);
  }

  private static int executeStatusCommand(CommandContext<ServerCommandSource> context, String groupName) {
    List<String> groups = ModState.listGroups();
    if (groups.isEmpty()) {
      context.getSource().sendFeedback(() -> TextUtils.getColoredText("No groups exist.", Formatting.GRAY), false);
      return 0;
    }

    if (groupName != null && !groups.contains(groupName)) {
      context.getSource().sendError(Text.literal("No group named '" + groupName + "' exists."));
      return 0;
    }

    MinecraftServer server = context.getSource().getServer();
    MutableText content = Text.literal("");

    if (groupName != null) {
      Optional<ShuffleGroup> groupOpt = ModState.getGroup(groupName);
      if (groupOpt.isPresent()) {
        ShuffleGroup group = groupOpt.get();
        List<UUID> playerUuids = ModState.getPlayersInGroup(groupName);

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

        if (!playerUuids.isEmpty()) {
          content.append(Text.literal("\n"));
          content.append(TextUtils.getColoredText("Players: ", Formatting.GRAY));

          for (int i = 0; i < playerUuids.size(); i++) {
            if (i > 0) {
              content.append(TextUtils.getColoredText(", ", Formatting.GRAY));
            }

            UUID uuid = playerUuids.get(i);
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            String playerName;
            boolean isCurrentPlayer = context.getSource().isExecutedByPlayer() &&
                context.getSource().getPlayer() != null &&
                context.getSource().getPlayer().getUuid().equals(uuid);

            if (player != null) {
              playerName = player.getName().getString();
              if (isCurrentPlayer) {
                playerName += " (you)";
              }
            } else {
              String storedName = ModState.getPlayerName(uuid);
              if (storedName != null) {
                playerName = storedName + " (offline)";
              } else {
                playerName = "[Offline: " + uuid.toString().substring(0, 8) + "...]";
              }
            }

            content.append(TextUtils.getColoredText(playerName, Formatting.GRAY));
          }
        }
      }
    } else {
      content.append(TextUtils.getColoredText("All Groups", Formatting.GOLD));
      content.append(TextUtils.getColoredText(" (", Formatting.GRAY));
      content.append(TextUtils.getColoredText(String.valueOf(groups.size()), Formatting.YELLOW));
      content.append(TextUtils.getColoredText(")", Formatting.GRAY));

      boolean first = true;
      for (String currentGroupName : groups) {
        Optional<ShuffleGroup> groupOpt = ModState.getGroup(currentGroupName);
        if (groupOpt.isPresent()) {
          ShuffleGroup group = groupOpt.get();
          List<UUID> playerUuids = ModState.getPlayersInGroup(currentGroupName);

          content.append(Text.literal("\n"));
          if (first) {
            first = false;
          }

          content.append(TextUtils.getColoredText("Group ", Formatting.GOLD));
          content.append(TextUtils.getColoredText("'" + currentGroupName + "'", Formatting.YELLOW));
          content.append(TextUtils.getColoredText(" (", Formatting.GRAY));
          content.append(TextUtils.getColoredText(
              group.isEnabled() ? "Enabled" : "Disabled",
              group.isEnabled() ? Formatting.GREEN : Formatting.RED));
          content.append(TextUtils.getColoredText(", Interval: ", Formatting.GRAY));
          content.append(TextUtils.getColoredText(String.valueOf(group.getInterval()), Formatting.YELLOW));
          content.append(TextUtils.getColoredText(" ticks, Players: ", Formatting.GRAY));
          content.append(TextUtils.getColoredText(String.valueOf(playerUuids.size()), Formatting.YELLOW));
          content.append(TextUtils.getColoredText(")", Formatting.GRAY));

          if (!playerUuids.isEmpty()) {
            content.append(Text.literal("\n"));
            content.append(TextUtils.getColoredText("Players: ", Formatting.GRAY));

            for (int i = 0; i < playerUuids.size(); i++) {
              if (i > 0) {
                content.append(TextUtils.getColoredText(", ", Formatting.GRAY));
              }

              UUID uuid = playerUuids.get(i);
              ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
              String playerName;
              boolean isCurrentPlayer = context.getSource().isExecutedByPlayer() &&
                  context.getSource().getPlayer() != null &&
                  context.getSource().getPlayer().getUuid().equals(uuid);

              if (player != null) {
                playerName = player.getName().getString();
                if (isCurrentPlayer) {
                  playerName += " (you)";
                }
              } else {
                String storedName = ModState.getPlayerName(uuid);
                if (storedName != null) {
                  playerName = storedName + " (offline)";
                } else {
                  playerName = "[Offline: " + uuid.toString().substring(0, 8) + "...]";
                }
              }

              content.append(TextUtils.getColoredText(playerName, Formatting.GRAY));
            }
          }
        }
      }
    }

    context.getSource().sendFeedback(() -> content, false);
    return 1;
  }
}