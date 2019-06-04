package com.misterpemodder.shulkerboxtooltip.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The type of preview to draw.
 * @since 1.3.0
 */
@Environment(EnvType.CLIENT)
public enum PreviewType {
  NO_PREVIEW, COMPACT, FULL;
}
