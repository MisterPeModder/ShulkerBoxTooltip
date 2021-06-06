package com.misterpemodder.shulkerboxtooltip.impl;

import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuCompat implements ModMenuApi {
  // private static final GuiRegistry defaultGuiRegistry =
  //     DefaultGuiTransformers.apply(DefaultGuiProviders.apply(new GuiRegistry()));

  /*
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
  return screen -> new ConfigScreenProvider<Configuration>(
    (ConfigManager<Configuration>) AutoConfig.getConfigHolder(Configuration.class),
    getGuiRegistryAccess(), screen).get();
  }*/

  /*
  private static GuiRegistryAccess getGuiRegistryAccess() {
    // We set our gui registry after the default one to override the default transformations.
    return new ComposedGuiRegistryAccess(defaultGuiRegistry,
        AutoConfig.getGuiRegistry(Configuration.class), new DefaultGuiRegistryAccess());
  }*/
}
