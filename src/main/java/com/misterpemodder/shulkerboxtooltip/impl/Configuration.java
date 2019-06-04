package com.misterpemodder.shulkerboxtooltip.impl;

import me.sargunvohra.mcmods.autoconfig1.ConfigData;
import me.sargunvohra.mcmods.autoconfig1.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1.annotation.ConfigEntry;
import me.shedaniel.cloth.gui.entries.EnumListEntry.Translatable;

@Config(name = "shulkerboxtooltip")
@Config.Gui.Background("minecraft:textures/block/purpur_block.png")
public class Configuration implements ConfigData {
  @ConfigEntry.Category("main")
  @ConfigEntry.Gui.TransitiveObject
  public MainCategory main = new MainCategory();

  @ConfigEntry.Category("compat")
  @ConfigEntry.Gui.TransitiveObject
  public CompatCategory compat = new CompatCategory();

  public static class MainCategory {
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean enablePreview = true;
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean lockPreview = false;
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean swapModes = false;
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean alwaysOn = false;
    @ConfigEntry.Gui.Tooltip(count = 4)
    public ShulkerBoxTooltipType tooltipType = ShulkerBoxTooltipType.MOD;
  }

  public static class CompatCategory {
    public boolean mpcsBackpacks;
  }

  public static enum ShulkerBoxTooltipType implements Translatable {
    VANILLA, MOD, NONE;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.tooltip_type." + name().toLowerCase();
    }
  }

}
