package samethope.inventory_shuffle.data;

/**
 * Enumeration of configurable options for shuffle groups
 */
public enum ShuffleOptions {
  ENABLED("enabled") {
    @Override
    public boolean applyTo(ShuffleGroup group, Object value) {
      if (value instanceof Boolean boolValue) {
        group.setEnabled(boolValue);
        return true;
      }
      return false;
    }
  },
  INTERVAL("interval") {
    @Override
    public boolean applyTo(ShuffleGroup group, Object value) {
      if (value instanceof Integer intValue) {
        group.setInterval(intValue);
        return true;
      } else if (value instanceof String strValue) {
        try {
          int intValue = Integer.parseInt(strValue);
          if (intValue > 0) {
            group.setInterval(intValue);
            return true;
          }
        } catch (NumberFormatException ignored) {
        }
      }
      return false;
    }
  },
  SHUFFLE_EMPTY_SLOTS("shuffleEmptySlots") {
    @Override
    public boolean applyTo(ShuffleGroup group, Object value) {
      if (value instanceof Boolean boolValue) {
        group.setShuffleEmptySlots(boolValue);
        return true;
      }
      return false;
    }
  },
  SHUFFLE_INVENTORY("shuffleInventory") {
    @Override
    public boolean applyTo(ShuffleGroup group, Object value) {
      if (value instanceof Boolean boolValue) {
        group.setShuffleInventory(boolValue);
        return true;
      }
      return false;
    }
  },
  SHUFFLE_HOTBAR("shuffleHotbar") {
    @Override
    public boolean applyTo(ShuffleGroup group, Object value) {
      if (value instanceof Boolean boolValue) {
        group.setShuffleHotbar(boolValue);
        return true;
      }
      return false;
    }
  },
  SHUFFLE_HAND("shuffleHand") {
    @Override
    public boolean applyTo(ShuffleGroup group, Object value) {
      if (value instanceof Boolean boolValue) {
        group.setShuffleHand(boolValue);
        return true;
      }
      return false;
    }
  },
  SHUFFLE_OFFHAND("shuffleOffhand") {
    @Override
    public boolean applyTo(ShuffleGroup group, Object value) {
      if (value instanceof Boolean boolValue) {
        group.setShuffleOffhand(boolValue);
        return true;
      }
      return false;
    }
  };

  private final String key;

  ShuffleOptions(String key) {
    this.key = key;
  }

  /**
   * Applies an option value to a group
   * 
   * @param group Group to apply the option to
   * @param value Value to apply
   * @return true if successful, false otherwise
   */
  public abstract boolean applyTo(ShuffleGroup group, Object value);

  /**
   * Gets an option by its key
   * 
   * @param key Key to look up
   * @return Matching option or null if not found
   */
  public static ShuffleOptions fromKey(String key) {
    for (ShuffleOptions option : values()) {
      if (option.key.equals(key)) {
        return option;
      }
    }
    return null;
  }
}