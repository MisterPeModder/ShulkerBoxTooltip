package com.misterpemodder.shulkerboxtooltip.impl.config;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
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
public final class Configuration implements ConfigData {
  @ConfigEntry.Category("preview")
  @ConfigEntry.Gui.TransitiveObject
  public PreviewCategory preview;

  @ConfigEntry.Category("tooltip")
  @ConfigEntry.Gui.TransitiveObject
  public TooltipCategory tooltip;

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
    if (ShulkerBoxTooltip.isClient())
      this.controls = new ControlsCategory();
    else
      this.controls = null;
    this.server = new ServerCategory();
  }

  public static class PreviewCategory implements Cloneable {
    @AutoTooltip
    @Comment("Toggles the shulker box preview.\n"
        + "(default value: true)")
    public boolean enable = true;

    @AutoTooltip
    @Comment("Locks the preview window above the tooltip.\n"
        + "When locked, the window will not adapt when out of screen.\n"
        + "(default value: false)")
    public boolean lock = false;

    @AutoTooltip
    @Comment("Swaps the preview modes.\n"
        + "If true, pressing the preview key will show the full preview instead.\n"
        + "(default value: false)")
    public boolean swapModes = false;

    @AutoTooltip
    @Comment("If on, the preview is always displayed, regardless of the preview key being pressed.\n"
        + "(default value: false)")
    public boolean alwaysOn = false;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("In compact mode, how should items with the same ID but different NBT data be compacted?\n"
        + "IGNORE: Ignores NBT data\n"
        + "FIRST_ITEM: Items are displayed as all having the same NBT as the first item\n"
        + "SEPARATE: Separates items with different NBT data\n"
        + "(default value: SEPARATE)")
    public CompactPreviewNbtBehavior compactPreviewNbtBehavior = CompactPreviewNbtBehavior.SEPARATE;

    @AutoTooltip
    @Comment("Controls whether the preview window should be colored.\n"
        + "(default value: true)")
    public boolean coloredPreview = true;

    @AutoTooltip
    @Validator(GreaterThanZero.class)
    @Comment("The max number of items in a row.\nMay not affect modded containers.\n"
        + "(default value: 9)")
    public int defaultMaxRowSize = 9;

    @AutoTooltip
    @ConfigEntry.Gui.RequiresRestart
    @Comment("If on, the client will try to send packets to servers to allow extra preview information such as ender chest previews.\n"
        + "(default value: true)")
    public boolean serverIntegration = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("The theme to use for preview windows.\n"
        + "MOD_AUTO: ShulkerBoxTooltip's style using the dark mode setting from LibGui, defaults to light theme if not present.\n"
        + "MOD_LIGHT: ShulkerBoxTooltip's style with vanilla colors.\n"
        + "MOD_DARK: ShulkeBoxTooltip's style with gray preview windows instead of white.\n"
        + "VANILLA: Mimics the style of vanilla bundle previews.\n"
        + "(default value: MOD_AUTO)")
    public Theme theme = Theme.MOD_AUTO;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("The position of the preview window.\n"
        + "INSIDE: Inside the item's tooltip.\n"
        + "OUTSIDE: Outside the item's tooltip, moves dependening on the screen borders.\n"
        + "OUTSIDE_TOP: Always at the top of the item's tooltip.\n"
        + "OUTSIDE_BOTTOM: Always at the bottom of the item's tooltip.\n"
        + "(default value: INSIDE)")
    public PreviewPosition position = PreviewPosition.INSIDE;

    @AutoTooltip
    @Comment("If on, large item counts in compact previews will be shortened.\n"
        + "(default value: true)")
    public boolean shortItemCounts = true;

    protected static PreviewCategory copyFrom(PreviewCategory source) {
      try {
        return (PreviewCategory) source.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static enum CompactPreviewNbtBehavior {
    IGNORE, FIRST_ITEM, SEPARATE;

    @Override
    public String toString() {
      return "shulkerboxtooltip.compactPreviewNbtBehavior." + this.name().toLowerCase();
    }
  }

  public static enum Theme {
    MOD_AUTO, MOD_LIGHT, MOD_DARK, VANILLA;

    @Override
    public String toString() {
      return "shulkerboxtooltip.theme." + this.name().toLowerCase();
    }
  }

  public static enum PreviewPosition {
    INSIDE, OUTSIDE, OUTSIDE_TOP, OUTSIDE_BOTTOM;

    @Override
    public String toString() {
      return "shulkerboxtooltip.preview_position." + this.name().toLowerCase();
    }
  }

  public static class TooltipCategory implements Cloneable {
    @AutoTooltip
    @Comment("Controls whether the key hints in the container's tooltip should be displayed.\n"
        + "(default value: true)")
    public boolean showKeyHints = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("The tooltip to use.\n"
        + "VANILLA: The vanilla tooltip (shows the first 5 items)\n"
        + "MOD: The mod's tooltip\n"
        + "NONE: No tooltip\n"
        + "(default value: MOD)")
    public ShulkerBoxTooltipType type = ShulkerBoxTooltipType.MOD;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("Shows info about the current loot table of the item if present.\n"
        + "Visible only when Tooltip Type is set to Modded.\n"
        + "HIDE: No loot table info, default.\n"
        + "SIMPLE: Displays whether the stack uses a loot table.\n"
        + "ADVANCED: Shows the loot table used by the item.\n"
        + "(default value: HIDE)")
    public LootTableInfoType lootTableInfoType = LootTableInfoType.HIDE;

    @AutoTooltip
    @Comment("If on, the mod hides the custom text on shulker box tooltips.\n"
        + "Use this option when a server-side preview datapack clashes with the mod.\n"
        + "(default value: false)")
    public boolean hideShulkerBoxLore = false;

    protected static TooltipCategory copyFrom(TooltipCategory source) {
      try {
        return (TooltipCategory) source.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static enum ShulkerBoxTooltipType {
    VANILLA, MOD, NONE;

    @Override
    public String toString() {
      return "shulkerboxtooltip.tooltipType." + this.name().toLowerCase();
    }
  }

  public static enum LootTableInfoType {
    HIDE, SIMPLE, ADVANCED;

    @Override
    public String toString() {
      return "shulkerboxtooltip.lootTableInfoType." + this.name().toLowerCase();
    }
  }

  @Environment(EnvType.CLIENT)
  public static class ControlsCategory implements Cloneable {
    @AutoTooltip
    @Comment("Press this key when hovering a container stack to open the preview window.\n"
        + "(default value: key.keyboard.left.shift)")
    public Key previewKey = Key.defaultPreviewKey();

    @AutoTooltip
    @Comment("Press this key when hovering a container stack to open the full preview window.\n"
        + "(default value: key.keyboard.left.alt)")
    public Key fullPreviewKey = Key.defaultFullPreviewKey();

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
    @Comment("If on, the server will be able to provide extra information about containers to the clients with the mod installed.\n"
        + "Disabling this option will disable all of the options below.\n"
        + "(default value: true)")
    public boolean clientIntegration = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Changes the way the ender chest content preview is synchronized.\n"
        + "NONE: No synchronization, prevents clients from seeing a preview of their ender chest.\n"
        + "ACTIVE: Ender chest contents are synchronized when changed.\n"
        + "PASSIVE: Ender chest contents are synchonized when the client opens a preview.\n"
        + "(default value: ACTIVE)")
    public EnderChestSyncType enderChestSyncType = EnderChestSyncType.ACTIVE;

    protected static ServerCategory copyFrom(ServerCategory source) {
      try {
        return (ServerCategory) source.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static enum EnderChestSyncType {
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
}
