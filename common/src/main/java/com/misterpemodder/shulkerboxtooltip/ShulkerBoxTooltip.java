package com.misterpemodder.shulkerboxtooltip;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.color.ColorRegistryImpl;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.provider.EnderChestPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.provider.PreviewProviderRegistryImpl;
import com.misterpemodder.shulkerboxtooltip.impl.provider.ShulkerBoxPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class ShulkerBoxTooltip implements ShulkerBoxTooltipApi {
  public static final String MOD_ID = "shulkerboxtooltip";
  public static final String MOD_NAME = "ShulkerBoxTooltip";
  public static final Logger LOGGER = LogManager.getFormatterLogger("ShulkerBoxTooltip");

  /**
   * The active config object, some of its properties are synced with the server.
   */
  public static Configuration config;
  /**
   * the actual config object, its values are never synced.
   */
  public static Configuration savedConfig;

  private static boolean registeredPlugins = false;

  /**
   * A list of all the vanilla shulker box items.
   */
  public static final Item[] SHULKER_BOX_ITEMS =
      new Item[] {Items.SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX,
          Items.LIGHT_BLUE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX, Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX,
          Items.GRAY_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
          Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.RED_SHULKER_BOX,
          Items.BLACK_SHULKER_BOX};

  public static void init() {
    savedConfig = ConfigurationHandler.register();
    config = ConfigurationHandler.copyOf(savedConfig);
    ServerNetworking.init();
  }

  private static void register(PreviewProviderRegistry registry, String id, PreviewProvider provider, Item... items) {
    registry.register(ShulkerBoxTooltipUtil.id(id), provider, items);
  }

  @Override
  public void registerProviders(PreviewProviderRegistry registry) {
    register(registry, "shulker_box", new ShulkerBoxPreviewProvider(), SHULKER_BOX_ITEMS);
    register(registry, "chest_like", new BlockEntityPreviewProvider(27, true), Items.CHEST, Items.TRAPPED_CHEST,
        Items.BARREL);
    register(registry, "furnace_like", new BlockEntityPreviewProvider(3, false, 1), Items.FURNACE, Items.BLAST_FURNACE,
        Items.SMOKER);
    register(registry, "dropper_like", new BlockEntityPreviewProvider(9, true, 3), Items.DROPPER, Items.DISPENSER);
    register(registry, "hopper", new BlockEntityPreviewProvider(5, true, 5), Items.HOPPER);
    register(registry, "brewing_stand", new BlockEntityPreviewProvider(5, false, 3), Items.BREWING_STAND);
    register(registry, "ender_chest", new EnderChestPreviewProvider(), Items.ENDER_CHEST);
  }

  @Override
  public void registerColors(ColorRegistry registry) {
    // @formatter:off
    registry.defaultCategory()
        .register("default", ColorKey.DEFAULT)
        .register("ender_chest", ColorKey.ENDER_CHEST);

    registry.category(ShulkerBoxTooltipUtil.id("shulker_boxes"))
        .register("shulker_box", ColorKey.SHULKER_BOX)
        .register("white_shulker_box", ColorKey.WHITE_SHULKER_BOX)
        .register("orange_shulker_box", ColorKey.ORANGE_SHULKER_BOX)
        .register("magenta_shulker_box", ColorKey.MAGENTA_SHULKER_BOX)
        .register("light_blue_shulker_box", ColorKey.LIGHT_BLUE_SHULKER_BOX)
        .register("yellow_shulker_box", ColorKey.YELLOW_SHULKER_BOX)
        .register("lime_shulker_box", ColorKey.LIME_SHULKER_BOX)
        .register("pink_shulker_box", ColorKey.PINK_SHULKER_BOX)
        .register("gray_shulker_box", ColorKey.GRAY_SHULKER_BOX)
        .register("light_gray_shulker_box", ColorKey.LIGHT_GRAY_SHULKER_BOX)
        .register("cyan_shulker_box", ColorKey.CYAN_SHULKER_BOX)
        .register("purple_shulker_box", ColorKey.PURPLE_SHULKER_BOX)
        .register("blue_shulker_box", ColorKey.BLUE_SHULKER_BOX)
        .register("brown_shulker_box", ColorKey.BROWN_SHULKER_BOX)
        .register("green_shulker_box", ColorKey.GREEN_SHULKER_BOX)
        .register("red_shulker_box", ColorKey.RED_SHULKER_BOX)
        .register("black_shulker_box", ColorKey.BLACK_SHULKER_BOX);
    // @formatter:on
  }

  /**
   * Initializes the plugins if needed.
   */
  public static void initPlugins() {
    if (registeredPlugins)
      return;
    registeredPlugins = true;

    List<PluginContainer> pluginList = getPluginContainers();
    String pluginText = switch (pluginList.size()) {
      case 0 -> "Loading %d plugins";
      case 1 -> "Loading %d plugin: %s";
      default -> "Loading %d plugins: %s";
    };
    ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME + "] " + pluginText, pluginList.size(),
        pluginList.stream().map(PluginContainer::modId).collect(Collectors.joining(", ")));

    Map<String, ShulkerBoxTooltipApi> plugins = new HashMap<>();

    for (PluginContainer plugin : pluginList) {
      try {
        plugins.put(plugin.modId(), plugin.apiImplSupplier().get());
      } catch (Exception e) {
        ShulkerBoxTooltip.LOGGER.error(
            "[" + ShulkerBoxTooltip.MOD_NAME + "] Failed to instantiate plugin of mod " + plugin.modId(), e);
      }
    }

    ColorRegistryImpl colorRegistry = ColorRegistryImpl.INSTANCE;
    PreviewProviderRegistryImpl providerRegistry = PreviewProviderRegistryImpl.INSTANCE;

    // Register all colors first
    for (var plugin : plugins.entrySet()) {
      var name = plugin.getKey();
      var instance = plugin.getValue();

      colorRegistry.resetRegisteredKeysCount();
      colorRegistry.setLocked(false);
      try {
        instance.registerColors(colorRegistry);
      } catch (Exception e) {
        ShulkerBoxTooltip.LOGGER.error("[" + ShulkerBoxTooltip.MOD_NAME + "] Failed to register colors for mod " + name,
            e);
        continue;
      }
      colorRegistry.setLocked(true);

      int registered = colorRegistry.registeredKeysCount();

      if (registered == 0)
        continue;

      String countText = registered == 1 ? "Registered %d color key for mod %s" : "Registered %d color keys for mod %s";

      ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME + "] " + countText, registered, name);
    }

    // Then register all preview providers
    for (var plugin : plugins.entrySet()) {
      var name = plugin.getKey();
      var instance = plugin.getValue();
      int prevSize = providerRegistry.getIds().size();
      int registered;

      providerRegistry.setLocked(false);
      try {
        instance.registerProviders(providerRegistry);
      } catch (Exception e) {
        ShulkerBoxTooltip.LOGGER.error(
            "[" + ShulkerBoxTooltip.MOD_NAME + "] Failed to register providers for mod " + name, e);
        continue;
      }
      providerRegistry.setLocked(true);
      registered = providerRegistry.getIds().size() - prevSize;

      String providerText =
          registered == 1 ? "Registered %d provider for mod %s" : "Registered %d providers for mod %s";

      ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME + "] " + providerText, registered, name);
    }
  }

  public record PluginContainer(String modId, Supplier<ShulkerBoxTooltipApi> apiImplSupplier) {
  }

  /**
   * @return Whether the current environment type (or Dist in forge terms) is the client.
   */
  @ExpectPlatform
  public static boolean isClient() {
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.isClient()");
  }

  /**
   * Get the current directory for game configuration files.
   *
   * @return the configuration directory.
   */
  @ExpectPlatform
  public static Path getConfigDir() {
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.getConfigDir()");
  }

  @ExpectPlatform
  public static List<PluginContainer> getPluginContainers() {
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.getPluginContainers()");
  }
}
