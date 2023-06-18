package com.misterpemodder.shulkerboxtooltip.impl.config;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.api.config.ItemStackMergingStrategy;
import com.misterpemodder.shulkerboxtooltip.api.config.PreviewConfiguration;
import com.misterpemodder.shulkerboxtooltip.impl.color.ColorRegistryImpl;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.AutoTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.Validator;
import com.misterpemodder.shulkerboxtooltip.impl.config.validators.GreaterThanZero;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Config(name = "shulkerboxtooltip")
@Config.Gui.Background("minecraft:textures/block/purpur_block.png")
@SuppressWarnings("CloneableClassWithoutClone")
public final class Configuration implements ConfigData, PreviewConfiguration {
  @ConfigEntry.Category("preview")
  @ConfigEntry.Gui.TransitiveObject
  public PreviewCategory preview;

  @ConfigEntry.Category("tooltip")
  @ConfigEntry.Gui.TransitiveObject
  public TooltipCategory tooltip;

  @ConfigEntry.Category("colors")
  @ConfigEntry.Gui.TransitiveObject
  @Environment(EnvType.CLIENT)
  public ColorsCategory colors;

  @ConfigEntry.Category("controls")
  @ConfigEntry.Gui.TransitiveObject
  @Environment(EnvType.CLIENT)
  public ControlsCategory controls;

  @ConfigEntry.Category("server")
  @ConfigEntry.Gui.TransitiveObject
  public ServerCategory server;

  public Configuration() {
    this.preview = new PreviewCategory();
    this.tooltip = new TooltipCategory();
    if (ShulkerBoxTooltip.isClient()) {
      this.colors = new ColorsCategory();
      this.controls = new ControlsCategory();
    }
    this.server = new ServerCategory();
  }

  public static class PreviewCategory implements Cloneable {
    @AutoTooltip
    @Comment("""
        Toggles the shulker box preview.
        (default value: true)""")
    public boolean enable = true;

    @AutoTooltip
    @Comment("""
        Swaps the preview modes.
        If true, pressing the preview key will show the full preview instead.
        (default value: false)""")
    public boolean swapModes = false;

    @AutoTooltip
    @Comment("""
        If on, the preview is always displayed, regardless of the preview key being pressed.
        (default value: false)""")
    public boolean alwaysOn = false;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("""
        In compact mode, how should items with the same ID but different NBT data be compacted?
        IGNORE: Ignores NBT data
        FIRST_ITEM: Items are displayed as all having the same NBT as the first item
        SEPARATE: Separates items with different NBT data
        (default value: SEPARATE)""")
    public ItemStackMergingStrategy compactPreviewNbtBehavior = ItemStackMergingStrategy.SEPARATE;

    @AutoTooltip
    @Validator(GreaterThanZero.class)
    @Comment("""
        The max number of items in a row.
        May not affect modded containers.
        (default value: 9)""")
    public int defaultMaxRowSize = 9;

    @AutoTooltip
    @ConfigEntry.Gui.RequiresRestart
    @Comment("""
        If on, the client will try to send packets to servers to allow extra preview information such as ender chest previews.
        (default value: true)
        """)
    public boolean serverIntegration = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("""
        The theme to use for preview windows.
        SHULKERBOXTOOLTIP: ShulkerBoxTooltip's default look and feel.
        VANILLA: Mimics the style of vanilla bundle previews.
        (default value: SHULKERBOXTOOLTIP)""")
    public Theme theme = Theme.SHULKERBOXTOOLTIP;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("""
        The position of the preview window.
        INSIDE: Inside the item's tooltip.
        OUTSIDE: Outside the item's tooltip, moves depending on the screen borders.
        OUTSIDE_TOP: Always at the top of the item's tooltip.
        OUTSIDE_BOTTOM: Always at the bottom of the item's tooltip.
        (default value: INSIDE)""")
    public PreviewPosition position = PreviewPosition.INSIDE;

    @AutoTooltip
    @Comment("""
        If on, large item counts in compact previews will be shortened.
        (default value: true)""")
    public boolean shortItemCounts = true;

    protected static PreviewCategory copyFrom(PreviewCategory source) {
      try {
        return (PreviewCategory) source.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public enum Theme {
    SHULKERBOXTOOLTIP, VANILLA;

    @Override
    public String toString() {
      return "shulkerboxtooltip.theme." + this.name().toLowerCase();
    }
  }

  public enum PreviewPosition {
    INSIDE, OUTSIDE, OUTSIDE_TOP, OUTSIDE_BOTTOM;

    @Override
    public String toString() {
      return "shulkerboxtooltip.preview_position." + this.name().toLowerCase();
    }
  }


  public static class TooltipCategory implements Cloneable {
    @AutoTooltip
    @Comment("""
        Controls whether the key hints in the container's tooltip should be displayed.
        (default value: true)""")
    public boolean showKeyHints = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("""
        The tooltip to use.
        VANILLA: The vanilla tooltip (shows the first 5 items)
        MOD: The mod's tooltip
        NONE: No tooltip
        (default value: MOD)""")
    public ShulkerBoxTooltipType type = ShulkerBoxTooltipType.MOD;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("""
        Shows info about the current loot table of the item if present.
        Visible only when Tooltip Type is set to Modded.
        HIDE: No loot table info, default.
        SIMPLE: Displays whether the stack uses a loot table.
        ADVANCED: Shows the loot table used by the item.
        (default value: HIDE)""")
    public LootTableInfoType lootTableInfoType = LootTableInfoType.HIDE;

    @AutoTooltip
    @Comment("""
        If on, the mod hides the custom text on shulker box tooltips.
        Use this option when a server-side preview data pack clashes with the mod.
        (default value: false)""")
    public boolean hideShulkerBoxLore = false;

    protected static TooltipCategory copyFrom(TooltipCategory source) {
      try {
        return (TooltipCategory) source.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }


  public enum ShulkerBoxTooltipType {
    VANILLA, MOD, NONE;

    @Override
    public String toString() {
      return "shulkerboxtooltip.tooltipType." + this.name().toLowerCase();
    }
  }


  public enum LootTableInfoType {
    HIDE, SIMPLE, ADVANCED;

    @Override
    public String toString() {
      return "shulkerboxtooltip.lootTableInfoType." + this.name().toLowerCase();
    }
  }


  @Environment(EnvType.CLIENT)
  public static class ColorsCategory implements Cloneable {
    @AutoTooltip
    @Comment("""
        Controls whether the preview window should be colored.
        (default value: true)""")
    public boolean coloredPreview = true;

    public ColorRegistry colors = ColorRegistryImpl.INSTANCE;

    protected static ColorsCategory copyFrom(ColorsCategory source) {
      if (source == null)
        return null;
      try {
        return (ColorsCategory) source.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }


  @Environment(EnvType.CLIENT)
  public static class ControlsCategory implements Cloneable {
    @AutoTooltip
    @Comment("""
        Press this key when hovering a container stack to open the preview window.
        (default value: key.keyboard.left.shift)""")
    public Key previewKey = Key.defaultPreviewKey();

    @AutoTooltip
    @Comment("""
        Press this key when hovering a container stack to open the full preview window.
        (default value: key.keyboard.left.alt)""")
    public Key fullPreviewKey = Key.defaultFullPreviewKey();

    @AutoTooltip
    @Comment("""
        Hold this key when previewing a stack to lock the tooltip.
        (default value: key.keyboard.left.control)""")
    public Key lockTooltipKey = Key.defaultLockTooltipKey();

    protected static ControlsCategory copyFrom(ControlsCategory source) {
      if (source == null)
        return null;
      try {
        return (ControlsCategory) source.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }


  public static class ServerCategory implements Cloneable {
    @AutoTooltip
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.RequiresRestart
    @Comment("""
        If on, the server will be able to provide extra information about containers to the clients with the mod installed.
        Disabling this option will disable all the options below.
        (default value: true)
        """)
    public boolean clientIntegration = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.RequiresRestart
    @Comment("""
        Changes the way the ender chest content preview is synchronized.
        NONE: No synchronization, prevents clients from seeing a preview of their ender chest.
        ACTIVE: Ender chest contents are synchronized when changed.
        PASSIVE: Ender chest contents are synchronized when the client opens a preview.
        (default value: ACTIVE)""")
    public EnderChestSyncType enderChestSyncType = EnderChestSyncType.ACTIVE;

    protected static ServerCategory copyFrom(ServerCategory source) {
      try {
        return (ServerCategory) source.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }


  public enum EnderChestSyncType {
    NONE, ACTIVE, PASSIVE;

    @Override
    public String toString() {
      return "shulkerboxtooltip.enderChestSyncType." + this.name().toLowerCase();
    }
  }

  @Override
  public void validatePostLoad() throws ValidationException {
    ConfigurationHandler.validate(this);
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
    return this.colors.coloredPreview;
  }
}