package com.misterpemodder.shulkerboxtooltip.api.config;

/**
 * The theme to use for preview windows.
 *
 * @since ?.?.?
 */
public enum Theme {
  /**
   * ShulkerBoxTooltip's custom look and feel.
   *
   * @since ?.?.?
   */
  SHULKERBOXTOOLTIP,
  /**
   * Vanilla's default look and feel.
   *
   * @since ?.?.?
   */
  VANILLA;

  @Override
  public String toString() {
    return "shulkerboxtooltip.config.theme." + this.name().toLowerCase();
  }
}
