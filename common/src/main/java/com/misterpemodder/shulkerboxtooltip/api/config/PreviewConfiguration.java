package com.misterpemodder.shulkerboxtooltip.api.config;

import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;

/**
 * A read-only view of the preview configuration.
 * Maps to the "preview" section of the config file.
 *
 * @since 3.3.0
 */
public interface PreviewConfiguration {
  /**
   * @return The way items with the same ID but differing NBT tags should be merged.
   * @since 3.3.0
   */
  ItemStackMergingStrategy itemStackMergingStrategy();

  /**
   * @return The max number of items in a row. May not affect modded containers.
   * @since 3.3.0
   */
  int defaultMaxRowSize();

  /**
   * @return Whether to shorten large item counts using suffixes. (e.g. 1,000,000 -> 1M)
   * @since 3.3.0
   */
  boolean shortItemCounts();

  /**
   * @return Whether to use colors when rendering the preview decoration,
   * when false the renderer should use the default inventory color.
   * @see ColorKey#DEFAULT
   * @since 3.3.0
   */
  boolean useColors();
}
