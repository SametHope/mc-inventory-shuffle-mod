package samethope.inventory_shuffle.utils;

import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

@SuppressWarnings("unused")
public class TextUtils {
  /**
   * Returns a colored text based on the provided string and color.
   *
   * @param message The text message to be colored.
   * @param color   The color from the Formatting enum.
   * @return Colored text.
   */
  public static MutableText getColoredText(String message, Formatting color) {
    return Text.literal(message).setStyle(Style.EMPTY.withColor(color));
  }

  /**
   * Returns a bold text.
   *
   * @param message The text message to be bold.
   * @return Bold text.
   */
  public static MutableText getBoldText(String message) {
    return Text.literal(message).setStyle(Style.EMPTY.withBold(true));
  }

  /**
   * Returns a bold and colored text.
   *
   * @param message The text message to be bold and colored.
   * @param color   The color from the Formatting enum.
   * @return Bold and colored text.
   */
  public static MutableText getBoldColoredText(String message, Formatting color) {
    return Text.literal(message).setStyle(Style.EMPTY.withBold(true).withColor(color));
  }

  /**
   * Returns an underlined text.
   *
   * @param message The text message to be underlined.
   * @return Underlined text.
   */
  public static MutableText getUnderlinedText(String message) {
    return Text.literal(message).setStyle(Style.EMPTY.withUnderline(true));
  }

  /**
   * Returns an underlined and colored text.
   *
   * @param message The text message to be underlined and colored.
   * @param color   The color from the Formatting enum.
   * @return Underlined and colored text.
   */
  public static MutableText getUnderlinedColoredText(String message, Formatting color) {
    return Text.literal(message).setStyle(Style.EMPTY.withUnderline(true).withColor(color));
  }

  /**
   * Returns a text with click event.
   *
   * @param message The text message.
   * @param url     The URL to open when clicked.
   * @return Clickable text.
   */
  public static MutableText getClickableText(String message, String url) {
    return Text.literal(message).setStyle(Style.EMPTY
        .withClickEvent(new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.OPEN_URL, url)));
  }

  /**
   * Returns a text with click event and color.
   *
   * @param message The text message.
   * @param url     The URL to open when clicked.
   * @param color   The color from the Formatting enum.
   * @return Clickable and colored text.
   */
  public static MutableText getClickableColoredText(String message, String url, Formatting color) {
    return Text.literal(message)
        .setStyle(Style.EMPTY
            .withClickEvent(new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.OPEN_URL, url))
            .withColor(color));
  }

  /**
   * Returns a text with click event, color and underline.
   *
   * @param message The text message.
   * @param url     The URL to open when clicked.
   * @param color   The color from the Formatting enum.
   * @return Clickable, colored and underlined text.
   */
  public static MutableText getClickableColoredUnderlinedText(String message, String url, Formatting color) {
    return Text.literal(message).setStyle(Style.EMPTY
        .withClickEvent(new net.minecraft.text.ClickEvent(net.minecraft.text.ClickEvent.Action.OPEN_URL, url))
        .withColor(color)
        .withUnderline(true));
  }
}