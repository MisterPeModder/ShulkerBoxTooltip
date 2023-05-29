package com.misterpemodder.shulkerboxtooltip.api.config;

public enum ItemStackMergingStrategy {
  IGNORE, FIRST_ITEM, SEPARATE;

  @Override
  public String toString() {
    return "shulkerboxtooltip.compactPreviewNbtBehavior." + this.name().toLowerCase();
  }
}
