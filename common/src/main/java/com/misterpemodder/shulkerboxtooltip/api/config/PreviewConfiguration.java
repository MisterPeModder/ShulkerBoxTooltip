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
   * Controls the way items with the same ID but differing components should be merged.
   *
   * @return the stack merging strategy.
   * @since 3.3.0
   */
  ItemStackMergingStrategy itemStackMergingStrategy();

  /**
   * The max number of items in a preview row. May not affect modded containers.
   *
   * @return the default max row size.
   * @since 3.3.0
   */
  int defaultMaxRowSize();

  /**
   * Whether to shorten large item counts using suffixes. (e.g. 1,000,000 -> 1M).
   *
   * @return whether to shorten item counts.
   * @since 3.3.0
   */
  boolean shortItemCounts();

  /**
   * The ordering strategy for compact preview items.
   *
   * @return the ordering strategy to use in compact previews.
   * @since 5.3.0
   */
  CompactPreviewOrder compactPreviewOrder();

  /**
   * Whether to use colors when rendering the preview decoration,
   * when {@code false} the renderer should use the default inventory color.
   *
   * @return whether to use colors in the preview decoration.
   * @see ColorKey#DEFAULT
   * @since 3.3.0
   */
  boolean useColors();
}
