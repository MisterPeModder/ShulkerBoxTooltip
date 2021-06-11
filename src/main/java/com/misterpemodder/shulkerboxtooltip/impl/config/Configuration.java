package com.misterpemodder.shulkerboxtooltip.impl.config;

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
import net.fabricmc.loader.api.FabricLoader;

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
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
      this.controls = new ControlsCategory();
    else
      this.controls = null;
    this.server = new ServerCategory();
  }

  public static class PreviewCategory implements Cloneable {
    @AutoTooltip
    @Comment("Toggles the shulker box preview")
    public boolean enable = true;

    @AutoTooltip
    @Comment("Locks the preview window above the tooltip.\n"
        + "When locked, the window will not adapt when out of screen.")
    public boolean lock = false;

    @AutoTooltip
    @Comment("Swaps the preview modes.\n"
        + "If true, pressing the preview key will show the full preview instead.")
    public boolean swapModes = false;

    @AutoTooltip
    @Comment("If on, the preview is always displayed, regardless of the preview key being pressed.")
    public boolean alwaysOn = false;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("In compact mode, how should items with the same ID but different NBT data be compacted?\n"
        + "IGNORE: Ignores NBT data\n"
        + "FIRST_ITEM: Items are displayed as all having the same NBT as the first item\n"
        + "SEPARATE: Separates items with different NBT data")
    public CompactPreviewNbtBehavior compactPreviewNbtBehavior = CompactPreviewNbtBehavior.SEPARATE;

    @AutoTooltip
    @Comment("Controls whether the preview window should be colored.")
    public boolean coloredPreview = true;

    @AutoTooltip
    @Validator(GreaterThanZero.class)
    @Comment("The max number of items in a row.\nMay not affect modded containers.")
    public int defaultMaxRowSize = 9;

    @AutoTooltip
    @ConfigEntry.Gui.RequiresRestart
    @Comment("If on, the client will try to send packets to servers to allow extra preview information such as ender chest previews.")
    public boolean serverIntegration = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("The theme to use.\n"
        + "AUTO: uses the dark mode setting from LibGui if present, defaults to light theme.\n"
        + "LIGHT: the regular vanilla-style theme\n"
        + "DARK: preview windows will be gray instead of white.")
    public Theme theme = Theme.AUTO;

    @AutoTooltip
    @Comment("If on, large item counts in compact previews will be shortened.")
    public boolean shortItemCounts = true;

    protected static PreviewCategory copyFrom(PreviewCategory source) {
      try {
        return (PreviewCategory) source.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static class TooltipCategory implements Cloneable {
    @AutoTooltip
    @Comment("Controls whether the key hints in the container's tooltip should be displayed.")
    public boolean showKeyHints = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("The tooltip to use.\nVANILLA: The vanilla tooltip (shows the first 5 items)\nMOD: The mod's tooltip\n"
        + "NONE: No tooltip")
    public ShulkerBoxTooltipType type = ShulkerBoxTooltipType.MOD;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("Shows info about the current loot table of the item if present.\n"
        + "Visible only when Tooltip Type is set to Modded.\n"
        + "HIDE: No loot table info, default.\n"
        + "SIMPLE: Displays whether the stack uses a loot table.\n"
        + "ADVANCED: Shows the loot table used by the item.")
    public LootTableInfoType lootTableInfoType = LootTableInfoType.HIDE;

    @AutoTooltip
    @Comment("If on, the mod hides the custom text on shulker box tooltips.\n"
        + "Use this option when a server-side preview datapack clashes with the mod.")
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

  public static enum CompactPreviewNbtBehavior {
    IGNORE, FIRST_ITEM, SEPARATE;

    @Override
    public String toString() {
      return "shulkerboxtooltip.compactPreviewNbtBehavior." + this.name().toLowerCase();
    }
  }

  public static enum LootTableInfoType {
    HIDE, SIMPLE, ADVANCED;

    @Override
    public String toString() {
      return "shulkerboxtooltip.lootTableInfoType." + this.name().toLowerCase();
    }
  }

  public static enum Theme {
    AUTO, LIGHT, DARK;

    @Override
    public String toString() {
      return "shulkerboxtooltip.theme." + this.name().toLowerCase();
    }
  }

  @Environment(EnvType.CLIENT)
  public static class ControlsCategory implements Cloneable {
    @AutoTooltip
    @Comment("Press this key when hovering a container stack to open the preview window.")
    public Key previewKey = Key.defaultPreviewKey();

    @AutoTooltip
    @Comment("Press this key when hovering a container stack to open the full preview window.")
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
        + "Disabling this option will disable all of the options below.")
    public boolean clientIntegration = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Changes the way the ender chest content preview is synchronized.\n"
        + "NONE: No synchronization, prevents clients from seeing a preview of their ender chest.\n"
        + "ACTIVE: Ender chest contents are synchronized when changed.\n"
        + "PASSIVE: Ender chest contents are synchonized when the client opens a preview.")
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
