package com.misterpemodder.shulkerboxtooltip.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ShulkerBoxTooltipImpl extends ShulkerBoxTooltip implements ModInitializer {
  @Override
  public void onInitialize() {
    super.onInitialize();
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

  /**
   * Implementation of {@link ShulkerBoxTooltip#getPluginContainers()}.
   */
  public static List<PluginContainer> getPluginContainers() {
    return FabricLoader.getInstance().getEntrypointContainers(ShulkerBoxTooltip.MOD_ID,
        ShulkerBoxTooltipApi.class).stream().map(entrypointContainer -> new PluginContainer(
        entrypointContainer.getProvider().getMetadata().getId(),
        entrypointContainer::getEntrypoint)).collect(Collectors.toList());
  }
}
