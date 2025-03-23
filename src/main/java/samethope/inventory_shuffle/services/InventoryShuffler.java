package samethope.inventory_shuffle.services;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import samethope.inventory_shuffle.data.ModState;
import samethope.inventory_shuffle.data.ShuffleGroup;

import java.util.*;

/**
 * Service class responsible for the core inventory shuffling functionality
 */
public class InventoryShuffler {
  private static final Random RANDOM = new Random();

  /**
   * Shuffles a player's inventory based on their group options
   * 
   * @param player The player whose inventory should be shuffled
   */
  public static void shuffleInventory(ServerPlayerEntity player) {
    if (player == null) {
      return;
    }

    Optional<ShuffleGroup> groupOpt = ModState.getPlayerGroup(player);
    if (groupOpt.isEmpty()) {
      return;
    }

    ShuffleGroup group = groupOpt.get();
    if (!group.isEnabled()) {
      return;
    }

    PlayerInventory inventory = player.getInventory();
    List<ItemStack> items = new ArrayList<>();
    List<Integer> slots = new ArrayList<>();
    List<Integer> emptySlots = new ArrayList<>();

    collectItemsForShuffle(group, inventory, items, slots, emptySlots);

    if (items.isEmpty()) {
      return;
    }

    performShuffle(group, inventory, items, slots, emptySlots);
  }

  /**
   * Collects items from specific inventory sections based on group options
   */
  private static void collectItemsForShuffle(ShuffleGroup group, PlayerInventory inventory,
      List<ItemStack> items, List<Integer> slots, List<Integer> emptySlots) {
    if (group.isShuffleHotbar()) {
      for (int i = 0; i < 9; i++) {
        if (group.isShuffleHand() || i != inventory.selectedSlot) {
          processSlot(inventory, i, items, slots, emptySlots);
        }
      }
    } else if (group.isShuffleHand()) {
      int handSlot = inventory.selectedSlot;
      processSlot(inventory, handSlot, items, slots, emptySlots);
    }

    if (group.isShuffleInventory()) {
      for (int i = 9; i < 36; i++) {
        processSlot(inventory, i, items, slots, emptySlots);
      }
    }

    if (group.isShuffleOffhand()) {
      processSlot(inventory, 40, items, slots, emptySlots);
    }
  }

  /**
   * Processes a single inventory slot for shuffling
   */
  private static void processSlot(PlayerInventory inventory, int slotIndex,
      List<ItemStack> items, List<Integer> slots, List<Integer> emptySlots) {
    ItemStack stack = inventory.getStack(slotIndex);
    if (!stack.isEmpty()) {
      items.add(stack.copy());
      slots.add(slotIndex);
      inventory.removeStack(slotIndex);
    } else {
      emptySlots.add(slotIndex);
    }
  }

  /**
   * Performs the actual inventory shuffling
   */
  private static void performShuffle(ShuffleGroup group, PlayerInventory inventory,
      List<ItemStack> items, List<Integer> slots, List<Integer> emptySlots) {
    Collections.shuffle(items, RANDOM);

    if (group.isShuffleEmptySlots()) {
      List<Integer> allSlots = new ArrayList<>(slots);
      allSlots.addAll(emptySlots);
      Collections.shuffle(allSlots, RANDOM);

      List<Integer> targetSlots = allSlots.subList(0, Math.min(items.size(), allSlots.size()));
      Collections.shuffle(targetSlots, RANDOM);

      for (int i = 0; i < items.size() && i < targetSlots.size(); i++) {
        inventory.setStack(targetSlots.get(i), items.get(i));
      }
    } else {
      for (int i = 0; i < items.size() && i < slots.size(); i++) {
        inventory.setStack(slots.get(i), items.get(i));
      }
    }
  }
}