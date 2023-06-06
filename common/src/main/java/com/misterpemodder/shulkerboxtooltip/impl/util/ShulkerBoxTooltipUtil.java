package com.misterpemodder.shulkerboxtooltip.impl.util;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import net.minecraft.util.Identifier;

public final class ShulkerBoxTooltipUtil {
  public static Identifier id(String id) {
    return new Identifier(ShulkerBoxTooltip.MOD_ID, id);
  }

  public static String abbreviateInteger(int count) {
    if (count == Integer.MIN_VALUE)
      return "-2G";
    if (count > -1000 && count < 1000)
      return Integer.toString(count);

    var str = new StringBuilder();

    if (count < 0) {
      str.append('-');
      count = -count;
    }
    char unit;
    int integral;
    int decimal = 0;

    switch ((int) Math.log10(count)) {
      case 3 -> {
        integral = count / 1_000;
        decimal = (count % 1_000) / 100;
        unit = 'k';
      }
      case 4, 5 -> {
        integral = count / 1_000;
        unit = 'k';
      }
      case 6 -> {
        integral = count / 1_000_000;
        decimal = (count % 1_000_000) / 100_000;
        unit = 'M';
      }
      case 7, 8 -> {
        integral = count / 1_000_000;
        unit = 'M';
      }
      default -> {
        integral = count / 1_000_000_000;
        decimal = (count % 1_000_000_000) / 100_000_000;
        unit = 'G';
      }
    }

    str.append(integral);
    if (decimal > 0)
      str.append('.').append(decimal);
    str.append(unit);
    return str.toString();
  }

  public static float[] rgbToComponents(int rgb) {
    int r = (rgb >> 16) & 0xFF;
    int g = (rgb >> 8) & 0xFF;
    int b = rgb & 0xFF;
    return new float[] {(float) r / 255F, (float) g / 255F, (float) b / 255F};
  }

  public static int componentsToRgb(float[] components) {
    int r = (int) (255F * components[0]);
    int g = (int) (255F * components[1]);
    int b = (int) (255F * components[2]);
    return (r << 16) | (g << 8) | b;
  }
}
