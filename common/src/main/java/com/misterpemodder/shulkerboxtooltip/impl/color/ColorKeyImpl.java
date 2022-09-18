package com.misterpemodder.shulkerboxtooltip.impl.color;

import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;

public record ColorKeyImpl(float[] rgbComponents) implements ColorKey {
  @Override
  public int rgb() {
    return ShulkerBoxTooltipUtil.componentsToRgb(this.rgbComponents);
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
}
