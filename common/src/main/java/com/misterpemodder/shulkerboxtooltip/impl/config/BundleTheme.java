package com.misterpemodder.shulkerboxtooltip.impl.config;

/**
 * The theme to use for bundle preview windows.
 */
public enum BundleTheme {
  /**
   * ShulkerBoxTooltip's custom look and feel.
   */
  SHULKERBOXTOOLTIP,
  /**
   * Fully disable ShulkerBoxTooltip's changes to bundles.
   */
  VANILLA,
  /**
   * Mimics vanilla theme, with enhancements like no size limit and preview locking.
   */
  VANILLA_PLUS;

  @Override
  public String toString() {
    return "shulkerboxtooltip.config.theme." + this.name().toLowerCase();
  }
}
