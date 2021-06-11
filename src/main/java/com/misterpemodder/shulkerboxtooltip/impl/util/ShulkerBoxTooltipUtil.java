package com.misterpemodder.shulkerboxtooltip.impl.util;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import net.minecraft.util.Identifier;

public final class ShulkerBoxTooltipUtil {
  public static Identifier id(String id) {
    return new Identifier(ShulkerBoxTooltip.MOD_ID, id);
  }

  public static String abrieviateInteger(int count) {
    if (count == Integer.MIN_VALUE)
      return "-2G";
    if (count > -1000 && count < 1000)
      return Integer.toString(count);

    var str = new StringBuilder();

    if (count < 0) {
      str.append('-');
      count = -count;
    }
    switch (((int) Math.log10(count)) + 1) {
      case 4, 5 -> {
        str.append(count / 1000);
        str.append('k');
      }
      case 6 -> {
        str.append('.');
        str.append(count / 100000);
        str.append('M');
      }
      case 7, 8 -> {
        str.append(count / 1000000);
        str.append('M');
      }
      case 9 -> {
        str.append('.');
        str.append(count / 100000000);
        str.append('G');
      }
      default -> {
        str.append(count / 1000000000);
        str.append('G');
      }
    }
    return str.toString();
  }
}
