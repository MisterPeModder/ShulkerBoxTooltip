package com.misterpemodder.shulkerboxtooltip.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.provider.PreviewProviderRegistryImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ShulkerBoxTooltipImpl extends ShulkerBoxTooltip implements ModInitializer {
  private static boolean registeredProviders = false;

  @Override
  public void onInitialize() {
    super.onInitialize();
  }

  /**
   * Implements {@link ShulkerBoxTooltip#initPreviewItemsMap()}.
   */
  public static void initPreviewItemsMap() {
    if (!registeredProviders) {
      registeredProviders = true;

      List<EntrypointContainer<ShulkerBoxTooltipApi>> plugins =
          FabricLoader.getInstance().getEntrypointContainers(ShulkerBoxTooltip.MOD_ID,
              ShulkerBoxTooltipApi.class);
      String pluginText = switch (plugins.size()) {
        case 0 -> "Loading %d plugins";
        case 1 -> "Loading %d plugin: %s";
        default -> "Loading %d plugins: %s";
      };

      ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME + "] " + pluginText,
          plugins.size(), plugins.stream().map(p -> p.getProvider().getMetadata().getId())
              .collect(Collectors.joining(", ")));
      for (EntrypointContainer<ShulkerBoxTooltipApi> container : plugins) {
        PreviewProviderRegistryImpl registry = PreviewProviderRegistryImpl.INSTANCE;
        int prevSize = registry.getIds().size();
        int registered;

        registry.setLocked(false);
        container.getEntrypoint().registerProviders(registry);
        registry.setLocked(true);
        registered = registry.getIds().size() - prevSize;

        String providerText = registered == 1 ?
            "Registered %d provider for mod %s" :
            "Registered %d providers for mod %s";

        ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME + "] " + providerText,
            registered, container.getProvider().getMetadata().getId());
      }
    }
  }

  /**
   * Implementation of {@link ShulkerBoxTooltip#isClient()}.
   */
  public static boolean isClient() {
    return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
  }

  /**
   * Implementation of {@link ShulkerBoxTooltip#getConfigDir()}.
   */
  public static Path getConfigDir() {
    return FabricLoader.getInstance().getConfigDir();
  }
}
