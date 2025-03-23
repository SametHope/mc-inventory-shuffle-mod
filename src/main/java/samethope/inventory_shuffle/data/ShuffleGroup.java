package samethope.inventory_shuffle.data;

import net.minecraft.nbt.NbtCompound;
import samethope.inventory_shuffle.config.InventoryShuffleConfig;

/**
 * Represents a group of players who share the same inventory shuffling options
 */
public class ShuffleGroup {
  private final String name;
  private boolean enabled = InventoryShuffleConfig.DEFAULT_ENABLED;
  private int interval = InventoryShuffleConfig.DEFAULT_SHUFFLE_INTERVAL;
  private boolean shuffleEmptySlots = InventoryShuffleConfig.DEFAULT_SHUFFLE_EMPTY_SLOTS;
  private boolean shuffleInventory = InventoryShuffleConfig.DEFAULT_SHUFFLE_INVENTORY;
  private boolean shuffleHotbar = InventoryShuffleConfig.DEFAULT_SHUFFLE_HOTBAR;
  private boolean shuffleHand = InventoryShuffleConfig.DEFAULT_SHUFFLE_HAND;
  private boolean shuffleOffhand = InventoryShuffleConfig.DEFAULT_SHUFFLE_OFFHAND;

  private int ticksLeft;

  /**
   * Creates a new shuffle group with default options
   * 
   * @param name The name of the group
   */
  public ShuffleGroup(String name) {
    this.name = name;
    this.ticksLeft = interval;
  }

  /**
   * Creates a shuffle group from NBT data
   * 
   * @param name The name of the group
   * @param nbt  The NBT compound containing the group's options
   */
  public ShuffleGroup(String name, NbtCompound nbt) {
    this.name = name;
    this.enabled = nbt.getBoolean("Enabled");
    this.interval = nbt.getInt("Interval");
    this.shuffleEmptySlots = nbt.getBoolean("ShuffleEmptySlots");
    this.shuffleInventory = nbt.getBoolean("ShuffleInventory");
    this.shuffleHotbar = nbt.getBoolean("ShuffleHotbar");
    this.shuffleHand = nbt.getBoolean("ShuffleHand");
    this.shuffleOffhand = nbt.getBoolean("ShuffleOffhand");
    this.ticksLeft = nbt.getInt("TicksLeft");
  }

  /**
   * Converts this group's options to NBT data
   * 
   * @return NBT compound containing the group's options
   */
  public NbtCompound toNbt() {
    NbtCompound nbt = new NbtCompound();
    nbt.putBoolean("Enabled", enabled);
    nbt.putInt("Interval", interval);
    nbt.putBoolean("ShuffleEmptySlots", shuffleEmptySlots);
    nbt.putBoolean("ShuffleInventory", shuffleInventory);
    nbt.putBoolean("ShuffleHotbar", shuffleHotbar);
    nbt.putBoolean("ShuffleHand", shuffleHand);
    nbt.putBoolean("ShuffleOffhand", shuffleOffhand);
    nbt.putInt("TicksLeft", ticksLeft);
    return nbt;
  }

  public String getName() {
    return name;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    this.interval = interval;
    this.ticksLeft = interval;
  }

  public boolean isShuffleEmptySlots() {
    return shuffleEmptySlots;
  }

  public void setShuffleEmptySlots(boolean shuffleEmptySlots) {
    this.shuffleEmptySlots = shuffleEmptySlots;
  }

  public boolean isShuffleInventory() {
    return shuffleInventory;
  }

  public void setShuffleInventory(boolean shuffleInventory) {
    this.shuffleInventory = shuffleInventory;
  }

  public boolean isShuffleHotbar() {
    return shuffleHotbar;
  }

  public void setShuffleHotbar(boolean shuffleHotbar) {
    this.shuffleHotbar = shuffleHotbar;
  }

  public boolean isShuffleHand() {
    return shuffleHand;
  }

  public void setShuffleHand(boolean shuffleHand) {
    this.shuffleHand = shuffleHand;
  }

  public boolean isShuffleOffhand() {
    return shuffleOffhand;
  }

  public void setShuffleOffhand(boolean shuffleOffhand) {
    this.shuffleOffhand = shuffleOffhand;
  }

  /**
   * Decrements the tick counter if shuffling is enabled
   */
  public void tick() {
    if (enabled && ticksLeft > 0) {
      ticksLeft--;
    }
  }

  /**
   * Checks if this group should shuffle inventories now
   * 
   * @return true if it's time to shuffle, false otherwise
   */
  public boolean shouldShuffle() {
    if (enabled && ticksLeft <= 0) {
      ticksLeft = interval;
      return true;
    }
    return false;
  }
}