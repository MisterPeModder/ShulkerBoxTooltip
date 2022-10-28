package com.misterpemodder.shulkerboxtooltip.api.color;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.color.ColorKeyImpl;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.ApiStatus;

/**
 * Player-customizable colors.
 * <p>
 * Once registered using the {@link ColorRegistry} instance given in {@link ShulkerBoxTooltipApi#registerColors(ColorRegistry)}
 * these will appear inside ShulkerBoxTooltip's configuration menu and file.
 *
 * @since 3.2.0
 */
@ApiStatus.NonExtendable
@Environment(EnvType.CLIENT)
public interface ColorKey {
  /**
   * The default inventory color.
   */
  ColorKey DEFAULT = ColorKey.ofRgb(0xffffff);

  /**
   * Color used by ender chest previews.
   */
  ColorKey ENDER_CHEST = ColorKey.ofRgb(0x0b4b41);

  /**
   * Undyed shulker box color.
   */
  ColorKey SHULKER_BOX = ColorKey.ofRgb(0x976797);
  /**
   * White shulker box color.
   */
  ColorKey WHITE_SHULKER_BOX = ColorKey.ofDye(DyeColor.WHITE);
  /**
   * Orange shulker box color.
   */
  ColorKey ORANGE_SHULKER_BOX = ColorKey.ofDye(DyeColor.ORANGE);
  /**
   * Magenta shulker box color.
   */
  ColorKey MAGENTA_SHULKER_BOX = ColorKey.ofDye(DyeColor.MAGENTA);
  /**
   * Light blue shulker box color.
   */
  ColorKey LIGHT_BLUE_SHULKER_BOX = ColorKey.ofDye(DyeColor.LIGHT_BLUE);
  /**
   * Yellow shulker box color.
   */
  ColorKey YELLOW_SHULKER_BOX = ColorKey.ofDye(DyeColor.YELLOW);
  /**
   * Lime shulker box color.
   */
  ColorKey LIME_SHULKER_BOX = ColorKey.ofDye(DyeColor.LIME);
  /**
   * Pink shulker box color.
   */
  ColorKey PINK_SHULKER_BOX = ColorKey.ofDye(DyeColor.PINK);
  /**
   * Gray shulker box color.
   */
  ColorKey GRAY_SHULKER_BOX = ColorKey.ofDye(DyeColor.GRAY);
  /**
   * § Light gray shulker box color.
   */
  ColorKey LIGHT_GRAY_SHULKER_BOX = ColorKey.ofDye(DyeColor.LIGHT_GRAY);
  /**
   * Cyan shulker box color.
   */
  ColorKey CYAN_SHULKER_BOX = ColorKey.ofDye(DyeColor.CYAN);
  /**
   * Purple shulker box color.
   */
  ColorKey PURPLE_SHULKER_BOX = ColorKey.ofDye(DyeColor.PURPLE);
  /**
   * Blue shulker box color.
   */
  ColorKey BLUE_SHULKER_BOX = ColorKey.ofDye(DyeColor.BLUE);
  /**
   * Brown shulker box color.
   */
  ColorKey BROWN_SHULKER_BOX = ColorKey.ofDye(DyeColor.BROWN);
  /**
   * Green shulker box color.
   */
  ColorKey GREEN_SHULKER_BOX = ColorKey.ofDye(DyeColor.GREEN);
  /**
   * Red shulker box color.
   */
  ColorKey RED_SHULKER_BOX = ColorKey.ofDye(DyeColor.RED);
  /**
   * Black shulker box color.
   */
  ColorKey BLACK_SHULKER_BOX = ColorKey.ofDye(DyeColor.BLACK);

  /**
   * Gets the value of this color key.
   *
   * @return The value of this key as an RGB-encoded integer.
   * @since 3.2.0
   */
  int rgb();

  /**
   * Gets the value of this color key.
   *
   * @return The value of this key as an array of three RGB float components.
   * @since 3.2.0
   */
  float[] rgbComponents();

  /**
   * Gets the default value of this color key.
   *
   * @return The default value of this key as an RGB-encoded integer.
   * @since 3.2.0
   */
  int defaultRgb();

  /**
   * Gets the default value of this color key.
   *
   * @return The default value of this key as an array of three RGB float components.
   * @since 3.2.0
   */
  float[] defaultRgbComponents();

  /**
   * Changes the color of this key using an RGB-encoded integer.
   *
   * @param rgb An ARGB integer, the alpha channel is ignored.
   * @since 3.2.0
   */
  void setRgb(int rgb);

  /**
   * Changes the color of this key using three RGB float component.
   *
   * @param rgb An array of three color channels, each value must range between 0 and 1 (inclusive).
   * @since 3.2.0
   */
  void setRgb(float[] rgb);

  /**
   * Creates a {@link ColorKey} instance from an existing one.
   * <p>
   * Modifications to the existing instance will not affect the new one and vice-versa.
   *
   * @param original The instance to copy.
   * @return A new {@link ColorKey} instance.
   * @since 3.2.0
   */
  static ColorKey copyOf(ColorKey original) {
    return ColorKey.ofRgb(original.rgbComponents());
  }

  /**
   * Creates a {@link ColorKey} instance from three float RGB components, each channel can have values ranging from 0 to 1 (inclusive).
   *
   * @param rgb The color components.
   * @return A new {@link ColorKey} instance.
   * @throws ArrayIndexOutOfBoundsException When the components array is too short.
   * @since 3.2.0
   */
  static ColorKey ofRgb(float[] rgb) {
    return new ColorKeyImpl(new float[] {rgb[0], rgb[1], rgb[2]}, new float[] {rgb[0], rgb[1], rgb[2]});
  }

  /**
   * Creates a {@link ColorKey} instance from an RGB integer.
   *
   * @param rgb An ARGB-encoded integer, the value of the alpha channel is ignored.
   * @return A new {@link ColorKey} instance.
   * @since 3.2.0
   */
  static ColorKey ofRgb(int rgb) {
    var components = ShulkerBoxTooltipUtil.rgbToComponents(rgb);
    return new ColorKeyImpl(components, new float[] {components[0], components[1], components[2]});
  }

  private static ColorKey ofDye(DyeColor dye) {
    float[] components = dye.getColorComponents();
    components[0] = Math.max(0.15f, components[0]);
    components[1] = Math.max(0.15f, components[1]);
    components[2] = Math.max(0.15f, components[2]);
    return new ColorKeyImpl(components, new float[] {components[0], components[1], components[2]});
  }
}
