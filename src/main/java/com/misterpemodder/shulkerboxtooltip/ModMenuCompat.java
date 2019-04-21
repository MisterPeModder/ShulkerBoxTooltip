package com.misterpemodder.shulkerboxtooltip;

import java.util.Optional;
import java.util.function.Supplier;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.Screen;

public class ModMenuCompat implements ModMenuApi {
  @Override
  public String getModId() {
    return "shulkerboxtooltip";
  }

  @Override
  public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
    return Optional.of(Configuration::buildConfigScreen);
  }
}
