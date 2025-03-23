package samethope.inventory_shuffle.config;

/**
 * Configuration constants for the Inventory Shuffle mod
 */
public class InventoryShuffleConfig {
  // Command options
  public static final String COMMAND_NAME = "invshuffle";
  public static final String COMMAND_ALIAS = "shuffleinv";
  public static final int COMMAND_PERMISSION_LEVEL = 4;

  // Default group options
  public static final String DEFAULT_GROUP_NAME = "default";
  public static final boolean DEFAULT_ENABLED = true;
  public static final int DEFAULT_SHUFFLE_INTERVAL = 10;
  public static final boolean DEFAULT_SHUFFLE_EMPTY_SLOTS = true;
  public static final boolean DEFAULT_SHUFFLE_INVENTORY = true;
  public static final boolean DEFAULT_SHUFFLE_HOTBAR = true;
  public static final boolean DEFAULT_SHUFFLE_HAND = false;
  public static final boolean DEFAULT_SHUFFLE_OFFHAND = false;

  // File options
  public static final String SAVE_FILE_EXTENSION = ".dat";

  // Debug options
  public static final boolean DEBUG_LOG_ENABLED = false;
}