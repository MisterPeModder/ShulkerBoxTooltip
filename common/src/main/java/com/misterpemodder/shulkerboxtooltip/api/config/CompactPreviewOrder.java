package com.misterpemodder.shulkerboxtooltip.api.config;

import com.misterpemodder.shulkerboxtooltip.impl.util.MergedItemStack;

import java.util.Comparator;

/**
 * The ordering strategy used for compact preview rendering.
 *
 * @since ?.?.?
 */
public enum CompactPreviewOrder {
  /**
   * Sort compact preview items by stack size, largest first.
   */
  STACK_SIZE,

  /**
   * Preserve the order of items as they appear in the container preview.
   */
  PREVIEW_ORDER;

  /**
   * Returns the {@link Comparator} that implements this ordering strategy.
   *
   * @return a comparator for {@link MergedItemStack}.
   * @since ?.?.?
   */
  public Comparator<MergedItemStack> toComparator() {
    return switch (this) {
      case STACK_SIZE -> Comparator.reverseOrder();
      case PREVIEW_ORDER -> Comparator.comparingInt(MergedItemStack::getFirstSlot);
    };
  }

  @Override
  public String toString() {
    return "shulkerboxtooltip.config.compact_preview_order." + this.name().toLowerCase();
  }
}
