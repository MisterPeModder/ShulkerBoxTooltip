package com.misterpemodder.shulkerboxtooltip.impl.config;

import java.util.Optional;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.AutoTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.Validator;
import com.misterpemodder.shulkerboxtooltip.impl.config.validators.GreaterThanZero;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry.Translatable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

@Config(name = "shulkerboxtooltip")
@Config.Gui.Background("minecraft:textures/block/purpur_block.png")
public class Configuration implements ConfigData {
  @ConfigEntry.Category("main")
  @ConfigEntry.Gui.TransitiveObject
  public MainCategory main;

  @ConfigEntry.Category("controls")
  @ConfigEntry.Gui.TransitiveObject
  public ControlsCategory controls;

  @ConfigEntry.Category("server")
  @ConfigEntry.Gui.TransitiveObject
  public ServerCategory server;

  public Configuration() {
    this.main = new MainCategory();
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
      this.controls = new ControlsCategory();
    else
      this.controls = null;
    this.server = new ServerCategory();
  }

  public static class MainCategory implements Cloneable {
    @AutoTooltip
    @Comment("Toggles the shulker box preview")
    public boolean enablePreview = true;

    @AutoTooltip
    @Comment("Locks the preview window above the tooltip.\nWhen locked, the window will not adapt when out of screen.")
    public boolean lockPreview = false;

    @AutoTooltip
    @Comment("Swaps the preview modes.\nIf true, pressing the preview key will show the full preview instead.")
    public boolean swapModes = false;

    @AutoTooltip
    @Comment("If on, the preview is always displayed, regardless of the preview key being pressed.")
    public boolean alwaysOn = false;

    @AutoTooltip
    @Comment("Controls whether the key hints in the container's tooltip should be displayed.")
    public boolean showKeyHints = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("The tooltip to use.\nVANILLA: The vanilla tooltip (shows the first 5 items)\nMOD: The mod's tooltip\nNONE: No tooltip")
    public ShulkerBoxTooltipType tooltipType = ShulkerBoxTooltipType.MOD;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("In compact mode, how should items with the same ID but different NBT data be compacted?\nIGNORE: Ignores NBT data\nFIRST_ITEM: Items are displayed as all having the same NBT as the first item\nSEPARATE: Separates items with different NBT data")
    public CompactPreviewTagBehavior compactPreviewTagBehavior = CompactPreviewTagBehavior.SEPARATE;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("Shows info about the current loot table of the item if present.\nVisible only when Tooltip Type is set to Modded.\nHIDE: No loot table info, default.\nSIMPLE: Displays whether the stack uses a loot table.\nADVANCED: Shows the loot table used by the item.")
    public LootTableInfoType lootTableInfoType = LootTableInfoType.HIDE;

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
    @Comment("The theme to use.\nAUTO: uses the dark mode setting from LibGui if present, defaults to light theme.\nLIGHT: the regular vanilla-style theme\nDARK: preview windows will be gray instead of white.")
    public Theme theme = Theme.AUTO;

    @AutoTooltip
    @Comment("If on, the mod hides the custom text on shulker box tooltips.\nUse this option when a server-side preview datapack clashes with the mod.")
    public boolean hideShulkerBoxLore = false;

    protected static MainCategory copyFrom(MainCategory source) {
      try {
        return (MainCategory) source.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static enum ShulkerBoxTooltipType implements Translatable {
    VANILLA, MOD, NONE;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.tooltipType." + this.name().toLowerCase();
    }
  }

  public static enum CompactPreviewTagBehavior implements Translatable {
    IGNORE, FIRST_ITEM, SEPARATE;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.compactPreviewTagBehavior." + this.name().toLowerCase();
    }
  }

  public static enum LootTableInfoType implements Translatable {
    HIDE, SIMPLE, ADVANCED;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.lootTableInfoType." + this.name().toLowerCase();
    }
  }

  public static enum Theme implements Translatable {
    AUTO, LIGHT, DARK;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.theme." + this.name().toLowerCase();
    }
  }

  public static class ControlsCategory implements Cloneable {
    @AutoTooltip
    @Comment("Press this key when hovering a container stack to open the preview window.")
    public Key previewKey = Key.defaultPreviewKey();

    @AutoTooltip
    @Comment("Press this key when hovering a container stack to open the full preview window.")
    public Key fullPreviewKey = Key.defaultFullPreviewKey();

    protected static ControlsCategory copyFrom(ControlsCategory source) {
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
    @Comment("If on, the server will be able to provide extra information about containers to the clients with the mod installed.\nDisabling this option will disable all of the options below.")
    public boolean clientIntegration = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Changes the way the ender chest content preview is synchronized.\nNONE: No synchronization, prevents clients from seeing a preview of their ender chest.\nACTIVE: Ender chest contents are synchronized when changed.\nPASSIVE: Ender chest contents are synchonized when the client opens a preview.")
    public EnderChestSyncType enderChestSyncType = EnderChestSyncType.ACTIVE;

    protected static ServerCategory copyFrom(ServerCategory source) {
      try {
        return (ServerCategory) source.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static enum EnderChestSyncType implements Translatable {
    NONE, ACTIVE, PASSIVE;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.enderChestSyncType." + this.name().toLowerCase();
    }
  }

  @Override
  public void validatePostLoad() throws ValidationException {
    ConfigurationHandler.validate(this);
  }
}
