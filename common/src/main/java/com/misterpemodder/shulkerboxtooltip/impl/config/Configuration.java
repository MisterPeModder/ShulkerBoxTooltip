package com.misterpemodder.shulkerboxtooltip.impl.config;

import blue.endless.jankson.Comment;
import com.misterpemodder.shulkerboxtooltip.api.config.ItemStackMergingStrategy;
import com.misterpemodder.shulkerboxtooltip.api.config.PreviewConfiguration;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.ConfigCategory;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.RequiresRestart;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.Synchronize;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.Validator;
import com.misterpemodder.shulkerboxtooltip.impl.config.validators.GreaterThanZero;

public class Configuration implements PreviewConfiguration {
  @ConfigCategory(ordinal = 1)
  public PreviewCategory preview;

  @ConfigCategory(ordinal = 2)
  public TooltipCategory tooltip;

  @ConfigCategory(ordinal = 5)
  public ServerCategory server;

  public Configuration() {
    this.preview = new PreviewCategory();
    this.tooltip = new TooltipCategory();
    this.server = new ServerCategory();
  }

  public static class PreviewCategory {
    @Comment("""
        Toggles the shulker box preview.
        (default value: true)""")
    public boolean enable = true;

    @Comment("""
        Swaps the preview modes.
        If true, pressing the preview key will show the full preview instead.
        (default value: false)""")
    public boolean swapModes = false;

    @Comment("""
        If on, the preview is always displayed, regardless of the preview key being pressed.
        (default value: false)""")
    public boolean alwaysOn = false;

    @Comment("""
        In compact mode, how should items with the same ID but different component data be compacted?
        IGNORE: Ignores component data
        FIRST_ITEM: Items are displayed as all having the same component as the first item
        SEPARATE: Separates items with different component data
        (default value: SEPARATE)""")
    public ItemStackMergingStrategy compactPreviewNbtBehavior = ItemStackMergingStrategy.SEPARATE;

    @Validator(GreaterThanZero.class)
    @Comment("""
        The max number of items in a row.
        May not affect modded containers.
        (default value: 9)""")
    public int defaultMaxRowSize = 9;

    @RequiresRestart
    @Comment("""
        If on, the client will try to send packets to servers to allow extra preview information such as ender chest previews.
        (default value: true)
        """)
    public boolean serverIntegration = true;

    @Comment("""
        The theme to use for preview windows.
        SHULKERBOXTOOLTIP: ShulkerBoxTooltip's default look and feel.
        VANILLA: Mimics the style of vanilla bundle previews.
        (default value: SHULKERBOXTOOLTIP)""")
    public Theme theme = Theme.SHULKERBOXTOOLTIP;

    @Comment("""
        The position of the preview window.
        INSIDE: Inside the item's tooltip.
        OUTSIDE: Outside the item's tooltip, moves depending on the screen borders.
        OUTSIDE_TOP: Always at the top of the item's tooltip.
        OUTSIDE_BOTTOM: Always at the bottom of the item's tooltip.
        (default value: INSIDE)""")
    public PreviewPosition position = PreviewPosition.INSIDE;

    @Comment("""
        If on, large item counts in compact previews will be shortened.
        (default value: true)""")
    public boolean shortItemCounts = true;

    @Comment("""
        If on, items with the container data component that are not explicitly supported
        will still show a preview, as if they were a shulker box.
        (default value: true)""")
    public boolean genericContainerPreview = true;
  }


  public enum Theme {
    SHULKERBOXTOOLTIP, VANILLA;

    @Override
    public String toString() {
      return "shulkerboxtooltip.config.theme." + this.name().toLowerCase();
    }
  }


  public enum PreviewPosition {
    INSIDE, OUTSIDE, OUTSIDE_TOP, OUTSIDE_BOTTOM;

    @Override
    public String toString() {
      return "shulkerboxtooltip.config.preview_position." + this.name().toLowerCase();
    }
  }


  public static class TooltipCategory {
    @Comment("""
        Controls whether the key hints in the container's tooltip should be displayed.
        (default value: true)""")
    public boolean showKeyHints = true;

    @Comment("""
        The tooltip to use.
        VANILLA: The vanilla tooltip (shows the first 5 items)
        MOD: The mod's tooltip
        NONE: No tooltip
        (default value: MOD)""")
    public ShulkerBoxTooltipType type = ShulkerBoxTooltipType.MOD;

    @Comment("""
        Shows info about the current loot table of the item if present.
        Visible only when Tooltip Type is set to Modded.
        HIDE: No loot table info, default.
        SIMPLE: Displays whether the stack uses a loot table.
        ADVANCED: Shows the loot table used by the item.
        (default value: HIDE)""")
    public LootTableInfoType lootTableInfoType = LootTableInfoType.HIDE;

    @Comment("""
        If on, the mod hides the custom text on shulker box tooltips.
        Use this option when a server-side preview data pack clashes with the mod.
        (default value: false)""")
    public boolean hideShulkerBoxLore = false;
  }


  public enum ShulkerBoxTooltipType {
    VANILLA, MOD, NONE;

    @Override
    public String toString() {
      return "shulkerboxtooltip.config.tooltip_type." + this.name().toLowerCase();
    }
  }


  public enum LootTableInfoType {
    HIDE, SIMPLE, ADVANCED;

    @Override
    public String toString() {
      return "shulkerboxtooltip.config.loot_table_info_type." + this.name().toLowerCase();
    }
  }


  public static class ServerCategory {
    @Synchronize
    @RequiresRestart
    @Comment("""
        If on, the server will be able to provide extra information about containers to the clients with the mod installed.
        Disabling this option will disable all the options below.
        (default value: true)
        """)
    public boolean clientIntegration = true;

    @Synchronize
    @RequiresRestart
    @Comment("""
        Changes the way the ender chest content preview is synchronized.
        NONE: No synchronization, prevents clients from seeing a preview of their ender chest.
        ACTIVE: Ender chest contents are synchronized when changed.
        PASSIVE: Ender chest contents are synchronized when the client opens a preview.
        (default value: ACTIVE)""")
    public EnderChestSyncType enderChestSyncType = EnderChestSyncType.ACTIVE;
  }


  public enum EnderChestSyncType {
    NONE, ACTIVE, PASSIVE;

    @Override
    public String toString() {
      return "shulkerboxtooltip.config.ender_chest_sync_type." + this.name().toLowerCase();
    }
  }

  @Override
  public ItemStackMergingStrategy itemStackMergingStrategy() {
    return this.preview.compactPreviewNbtBehavior;
  }

  @Override
  public int defaultMaxRowSize() {
    return this.preview.defaultMaxRowSize;
  }

  @Override
  public boolean shortItemCounts() {
    return this.preview.shortItemCounts;
  }

  @Override
  public boolean useColors() {
    return false;
  }
}
