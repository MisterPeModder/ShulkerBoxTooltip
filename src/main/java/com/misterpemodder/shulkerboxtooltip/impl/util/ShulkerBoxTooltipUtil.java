
package com.misterpemodder.shulkerboxtooltip.impl.util;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;

import net.minecraft.util.Identifier;

public final class ShulkerBoxTooltipUtil {
  public static Identifier id(String id) {
    return new Identifier(ShulkerBoxTooltip.MOD_ID, id);
  }
}
