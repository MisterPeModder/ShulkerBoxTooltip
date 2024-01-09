package com.misterpemodder.shulkerboxtooltip.impl.color;

import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record ColorKeyImpl(float[] rgbComponents, float[] defaultRgbComponents) implements ColorKey {
  @Override
  public int rgb() {
    return ShulkerBoxTooltipUtil.componentsToRgb(this.rgbComponents());
  }

  @Override
  public int defaultRgb() {
    return ShulkerBoxTooltipUtil.componentsToRgb(this.defaultRgbComponents());
  }

  @Override
  public void setRgb(int rgb) {
    this.setRgb(ShulkerBoxTooltipUtil.rgbToComponents(rgb));
  }

  @Override
  public void setRgb(float[] rgb) {
    this.rgbComponents[0] = rgb[0];
    this.rgbComponents[1] = rgb[1];
    this.rgbComponents[2] = rgb[2];
  }

  @Override
  public String toString() {
    return String.format("ColorKey(rgb=#%x, defaultRgb=#%x)", this.rgb(), this.defaultRgb());
  }
}
