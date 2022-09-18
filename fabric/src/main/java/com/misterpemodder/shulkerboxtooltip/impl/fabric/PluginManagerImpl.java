package com.misterpemodder.shulkerboxtooltip.impl.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.PluginManager;
import com.misterpemodder.shulkerboxtooltip.impl.PluginManager.PluginContainer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class PluginManagerImpl {
  /**
   * Implementation of {@link PluginManager#getPluginContainers()}.
   */
  public static List<PluginContainer> getPluginContainers() {
    return FabricLoader.getInstance()
        .getEntrypointContainers(ShulkerBoxTooltip.MOD_ID, ShulkerBoxTooltipApi.class)
        .stream()
        .map(entrypointContainer -> new PluginContainer(entrypointContainer.getProvider().getMetadata().getId(),
            entrypointContainer::getEntrypoint))
        .collect(Collectors.toList());
  }
}
