package com.misterpemodder.shulkerboxtooltip.impl;

import java.util.Optional;
import java.util.function.Supplier;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuCompat implements ModMenuApi {
  @Override
  public String getModId() {
    return "shulkerboxtooltip";
  }

  @Override
  public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
    return Optional.of(AutoConfig.getConfigScreen(Configuration.class, screen));
  }
}
