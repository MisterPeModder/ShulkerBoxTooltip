package com.misterpemodder.shulkerboxtooltip.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The type of preview to draw.
 *
 * @since 1.3.0
 */
@Environment(EnvType.CLIENT)
public enum PreviewType {
  /**
   * Preview is not present.
   */
  NO_PREVIEW,

  /**
   * Compact mode: similar items are grouped together and empty slots are not shown.
   */
  COMPACT,

  /**
   * Full mode: all stacks are shown in their respective slots, empty slots are also displayed.
   */
  FULL
}
