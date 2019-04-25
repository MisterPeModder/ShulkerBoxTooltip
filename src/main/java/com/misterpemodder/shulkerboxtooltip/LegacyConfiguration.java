package com.misterpemodder.shulkerboxtooltip;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.entries.BooleanListEntry;
import me.shedaniel.cloth.gui.entries.EnumListEntry;
import me.shedaniel.cloth.gui.entries.EnumListEntry.Translatable;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public final class LegacyConfiguration {
  private static final Logger LOGGER = LogManager.getLogger("ShulkerBoxTooltip");
  private static boolean lockPreview = false;
  private static boolean swapModes = false;
  private static boolean enablePreview = true;
  private static LegacyShulkerBoxTooltipType tooltipType = LegacyShulkerBoxTooltipType.MOD;

  public static void loadConfiguration() {
    Path configDir = getConfigDirectory();
    if (configDir == null)
      return;
    Path configFile = configDir.resolve("config.properties");
    if (!Files.exists(configFile)) {
      try {
        Files.copy(ShulkerBoxTooltip.class.getResourceAsStream("/config.properties"), configFile);
      } catch (IOException e) {
        LOGGER.error("Could not create default configuration file", e);
      }
    }

    try (Reader in = Files.newBufferedReader(configFile)) {
      Properties properties = new Properties();
      properties.load(in);
      enablePreview = parseBoolean(properties, "preview.enabled", true);
      lockPreview = parseBoolean(properties, "preview.position.lock", false);
      swapModes = parseBoolean(properties, "preview.modes.swap", false);
      tooltipType = parseTooltipType(properties, "tooltip.type", LegacyShulkerBoxTooltipType.MOD);
    } catch (IOException e) {
      LOGGER.error("Could not read configuration", e);
    }
  }

  public static void saveConfiguration() {
    Path configDir = getConfigDirectory();
    if (configDir == null)
      return;
    Path configFile = configDir.resolve("config.properties");
    try (Writer out = Files.newBufferedWriter(configFile)) {
      Properties properties = new Properties();
      properties.setProperty("preview.enabled", Boolean.toString(enablePreview));
      properties.setProperty("preview.position.lock", Boolean.toString(lockPreview));
      properties.setProperty("preview.modes.swap", Boolean.toString(swapModes));
      properties.setProperty("tooltip.type", Integer.toString(tooltipType.ordinal()));
      properties.store(out, null);
    } catch (IOException e) {
      LOGGER.error("Could not save configuration");
    }
  }

  @Nullable
  private static Path getConfigDirectory() {
    try {
      Path configDir = Paths.get(FabricLoader.getInstance().getConfigDirectory().toString(),
          "shulkerboxtooltip");
      Files.createDirectories(configDir);
      return configDir;
    } catch (IOException e) {
      LOGGER.error("Could not create config directory");
      return null;
    }
  }

  private static boolean parseBoolean(Properties properties, String key, boolean defaultValue) {
    String value = properties.getProperty(key);
    return value != null ? Boolean.parseBoolean(value) : defaultValue;
  }

  private static LegacyShulkerBoxTooltipType parseTooltipType(Properties properties, String key,
      LegacyShulkerBoxTooltipType defaultType) {
    String value = properties.getProperty(key);
    if (value != null) {
      try {
        int type = Integer.parseInt(value);
        if (type < 0 || type >= LegacyShulkerBoxTooltipType.values().length) {
          LOGGER
              .error("Invalid shulker box tooltip type, expected integer between 0 (inclusive) and "
                  + LegacyShulkerBoxTooltipType.values().length + " (inclusive).");
          return defaultType;
        }
        return LegacyShulkerBoxTooltipType.values()[type];
      } catch (NumberFormatException e) {
        LOGGER.error("Invalid value for key '" + key + "' in config, expected integer.");
      }
    }
    return defaultType;
  }

  public static Screen buildConfigScreen() {
    return buildConfigScreen(MinecraftClient.getInstance().currentScreen);
  }

  public static Screen buildConfigScreen(@Nullable Screen parent) {
    ConfigScreenBuilder builder = ConfigScreenBuilder.create(parent,
        "config.shulkerboxtooltip.title", c -> saveConfiguration());
    builder.setBackgroundTexture(
        new Identifier("shulkerboxtooltip", "textures/gui/config_background.png"));
    builder.addCategory("config.shulkerboxtooltip.category")
        .addOption(new BooleanListEntry("config.shulkerboxtooltip.enable_preview", enablePreview,
            "text.cloth-config.reset_value", () -> true, v -> enablePreview = v,
            getOptionTooltipSupplier("enable_preview")))
        .addOption(new BooleanListEntry("config.shulkerboxtooltip.lock_preview", lockPreview,
            "text.cloth-config.reset_value", () -> false, v -> lockPreview = v,
            getOptionTooltipSupplier("lock_preview")))
        .addOption(new BooleanListEntry("config.shulkerboxtooltip.swap_modes", swapModes,
            "text.cloth-config.reset_value", () -> false, v -> swapModes = v,
            getOptionTooltipSupplier("swap_modes")))
        .addOption(
            new EnumListEntry<LegacyShulkerBoxTooltipType>("config.shulkerboxtooltip.tooltip_type",
                LegacyShulkerBoxTooltipType.class, tooltipType, "text.cloth-config.reset_value",
                () -> LegacyShulkerBoxTooltipType.MOD, v -> tooltipType = v,
                EnumListEntry.DEFAULT_NAME_PROVIDER, getOptionTooltipSupplier("tooltip_type")));
    return builder.build();
  }

  private static Supplier<Optional<String[]>> getOptionTooltipSupplier(String key) {
    return () -> Optional
        .of(I18n.translate("config.shulkerboxtooltip." + key + ".desc").split("\\n"));
  }

  public static boolean isPreviewEnabled() {
    return enablePreview;
  }

  public static boolean isPreviewLocked() {
    return lockPreview;
  }

  public static boolean areModesSwapped() {
    return swapModes;
  }

  public static LegacyShulkerBoxTooltipType getTooltipType() {
    return tooltipType;
  }

  public static enum LegacyShulkerBoxTooltipType implements Translatable {
    VANILLA, MOD, NONE;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.tooltip_type." + name().toLowerCase();
    }
  }
}
