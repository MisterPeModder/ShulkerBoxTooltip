package com.misterpemodder.shulkerboxtooltip.api.config;

/**
 * The theme to use for preview windows.
 *
 * @since 5.3.0
 */
public enum Theme {
  /**
   * ShulkerBoxTooltip's custom look and feel.
   *
   * @since 5.3.0
   */
  SHULKERBOXTOOLTIP,
  /**
   * Vanilla's default look and feel.
   *
   * @since 5.3.0
   */
  VANILLA;

  @Override
  public String toString() {
    return "shulkerboxtooltip.config.theme." + this.name().toLowerCase();
  }
}
