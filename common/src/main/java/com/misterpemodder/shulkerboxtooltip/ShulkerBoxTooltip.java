package com.misterpemodder.shulkerboxtooltip;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
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
import org.jetbrains.annotations.Contract;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

  private static boolean registeredProviders = false;

  /**
   * A list of all the vanilla shulker box items.
   */
  public static final Item[] SHULKER_BOX_ITEMS =
      new Item[] {Items.SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX,
          Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX,
          Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
          Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
          Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
          Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX};

  public void onInitialize() {
    savedConfig = ConfigurationHandler.register();
    config = ConfigurationHandler.copyOf(savedConfig);
    ServerNetworking.init();
  }

  private static void register(PreviewProviderRegistry registry, String id,
      PreviewProvider provider, Item... items) {
    registry.register(ShulkerBoxTooltipUtil.id(id), provider, items);
  }

  @Override
  public void registerProviders(PreviewProviderRegistry registry) {
    register(registry, "shulker_box", new ShulkerBoxPreviewProvider(), SHULKER_BOX_ITEMS);
    register(registry, "chest_like", new BlockEntityPreviewProvider(27, true), Items.CHEST,
        Items.TRAPPED_CHEST, Items.BARREL);
    register(registry, "furnace_like", new BlockEntityPreviewProvider(3, false, 1), Items.FURNACE,
        Items.BLAST_FURNACE, Items.SMOKER);
    register(registry, "dropper_like", new BlockEntityPreviewProvider(9, true, 3), Items.DROPPER,
        Items.DISPENSER);
    register(registry, "hopper", new BlockEntityPreviewProvider(5, true, 5), Items.HOPPER);
    register(registry, "brewing_stand", new BlockEntityPreviewProvider(5, false, 3),
        Items.BREWING_STAND);
    register(registry, "ender_chest", new EnderChestPreviewProvider(), Items.ENDER_CHEST);
  }

  /**
   * If not present, creates the preview item map by registering the preview providers supplied
   * by the API implementations.
   */
  public static void initPreviewItemsMap() {
    if (registeredProviders)
      return;
    registeredProviders = true;

    List<PluginContainer> plugins = getPluginContainers();
    String pluginText = switch (plugins.size()) {
      case 0 -> "Loading %d plugins";
      case 1 -> "Loading %d plugin: %s";
      default -> "Loading %d plugins: %s";
    };

    ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME + "] " + pluginText,
        plugins.size(), plugins.stream().map(PluginContainer::modId)
            .collect(Collectors.joining(", ")));
    for (PluginContainer plugin : plugins) {
      PreviewProviderRegistryImpl registry = PreviewProviderRegistryImpl.INSTANCE;
      int prevSize = registry.getIds().size();
      int registered;

      registry.setLocked(false);
      plugin.apiImplSupplier.get().registerProviders(registry);
      registry.setLocked(true);
      registered = registry.getIds().size() - prevSize;

      String providerText = registered == 1 ?
          "Registered %d provider for mod %s" :
          "Registered %d providers for mod %s";

      ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME + "] " + providerText,
          registered, plugin.modId());
    }
  }

  public record PluginContainer(String modId, Supplier<ShulkerBoxTooltipApi> apiImplSupplier) {}

  /**
   * @return Whether the current environment type (or Dist in forge terms) is the client.
   */
  @ExpectPlatform
  @SuppressWarnings("Contract")
  @Contract(value = " -> _", pure = true)
  public static boolean isClient() {
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.isClient()");
  }

  /**
   * Get the current directory for game configuration files.
   *
   * @return the configuration directory.
   */
  @ExpectPlatform
  @SuppressWarnings("Contract")
  @Contract(value = " -> !null", pure = true)
  public static Path getConfigDir() {
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.getConfigDir()");
  }

  @ExpectPlatform
  @SuppressWarnings("Contract")
  @Contract(" -> !null")
  public static List<PluginContainer> getPluginContainers() {
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.getPluginContainers()");
  }
}
