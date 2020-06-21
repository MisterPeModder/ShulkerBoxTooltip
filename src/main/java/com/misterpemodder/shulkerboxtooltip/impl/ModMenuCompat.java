package com.misterpemodder.shulkerboxtooltip.impl;

import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigManager;
import me.sargunvohra.mcmods.autoconfig1u.gui.ConfigScreenProvider;
import me.sargunvohra.mcmods.autoconfig1u.gui.DefaultGuiProviders;
import me.sargunvohra.mcmods.autoconfig1u.gui.DefaultGuiTransformers;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.ComposedGuiRegistryAccess;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.DefaultGuiRegistryAccess;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.GuiRegistry;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.api.GuiRegistryAccess;

public class ModMenuCompat implements ModMenuApi {
  private static final GuiRegistry defaultGuiRegistry = DefaultGuiTransformers
      .apply(DefaultGuiProviders.apply(new GuiRegistry()));

  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return screen -> new ConfigScreenProvider<Configuration>(
        (ConfigManager<Configuration>) AutoConfig.getConfigHolder(Configuration.class), getGuiRegistryAccess(), screen)
            .get();
  }

  private static GuiRegistryAccess getGuiRegistryAccess() {
    // We set our gui registry after the dafault one to override the default transformations.
    return new ComposedGuiRegistryAccess(defaultGuiRegistry, AutoConfig.getGuiRegistry(Configuration.class),
        new DefaultGuiRegistryAccess());
  }
}
