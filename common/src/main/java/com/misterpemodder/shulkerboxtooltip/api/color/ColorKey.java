package com.misterpemodder.shulkerboxtooltip.api.color;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.color.ColorKeyImpl;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.DyeColor;

/**
 * User-customizable colors.
 * <p>
 * Once registered using the {@link ColorRegistry} instance given in {@link ShulkerBoxTooltipApi#registerColors(ColorRegistry)}
 * these will appear inside ShulkerBoxTooltip's configuration menu and file.
 *
 * @since 3.2.0
 */
@Environment(EnvType.CLIENT)
public interface ColorKey {
  ColorKey DEFAULT = ColorKey.ofRgb(0xffffff);

  ColorKey ENDER_CHEST = ColorKey.ofRgb(0x0b4b41);

  ColorKey SHULKER_BOX = ColorKey.ofRgb(0x976797);
  ColorKey WHITE_SHULKER_BOX = ColorKey.ofDye(DyeColor.WHITE);
  ColorKey ORANGE_SHULKER_BOX = ColorKey.ofDye(DyeColor.ORANGE);
  ColorKey MAGENTA_SHULKER_BOX = ColorKey.ofDye(DyeColor.MAGENTA);
  ColorKey LIGHT_BLUE_SHULKER_BOX = ColorKey.ofDye(DyeColor.LIGHT_BLUE);
  ColorKey YELLOW_SHULKER_BOX = ColorKey.ofDye(DyeColor.YELLOW);
  ColorKey LIME_SHULKER_BOX = ColorKey.ofDye(DyeColor.LIME);
  ColorKey PINK_SHULKER_BOX = ColorKey.ofDye(DyeColor.PINK);
  ColorKey GRAY_SHULKER_BOX = ColorKey.ofDye(DyeColor.GRAY);
  ColorKey LIGHT_GRAY_SHULKER_BOX = ColorKey.ofDye(DyeColor.LIGHT_GRAY);
  ColorKey CYAN_SHULKER_BOX = ColorKey.ofDye(DyeColor.CYAN);
  ColorKey PURPLE_SHULKER_BOX = ColorKey.ofDye(DyeColor.PURPLE);
  ColorKey BLUE_SHULKER_BOX = ColorKey.ofDye(DyeColor.BLUE);
  ColorKey BROWN_SHULKER_BOX = ColorKey.ofDye(DyeColor.BROWN);
  ColorKey GREEN_SHULKER_BOX = ColorKey.ofDye(DyeColor.GREEN);
  ColorKey RED_SHULKER_BOX = ColorKey.ofDye(DyeColor.RED);
  ColorKey BLACK_SHULKER_BOX = ColorKey.ofDye(DyeColor.BLACK);

  /**
   * @return The value of this key as an RGB-encoded integer.
   * @since 3.2.0
   */
  int rgb();

  /**
   * @return The value of this key as an array of three RGB float components.
   * @since 3.2.0
   */
  float[] rgbComponents();

  /**
   * Changes the color of this key using an RGB-encoded integer.
   *
   * @since 3.2.0
   */
  void setRgb(int rgb);

  /**
   * Changes the color of this key using three RGB float component.
   *
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
    return new ColorKeyImpl(new float[] {rgb[0], rgb[1], rgb[2]});
  }

  /**
   * Creates a {@link ColorKey} instance from an RGB integer.
   *
   * @param rgb An ARGB-encoded integer, the value of the alpha channel is ignored.
   * @return A new {@link ColorKey} instance.
   * @since 3.2.0
   */
  static ColorKey ofRgb(int rgb) {
    return new ColorKeyImpl(ShulkerBoxTooltipUtil.rgbToComponents(rgb));
  }

  private static ColorKey ofDye(DyeColor dye) {
    float[] components = dye.getColorComponents();
    return new ColorKeyImpl(
        new float[] {Math.max(0.15f, components[0]), Math.max(0.15f, components[1]), Math.max(0.15f, components[2])});
  }
}
