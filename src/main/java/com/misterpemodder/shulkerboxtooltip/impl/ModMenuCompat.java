package com.misterpemodder.shulkerboxtooltip.impl;

import java.util.function.Function;
// import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import io.github.prospector.modmenu.api.ModMenuApi;
// import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuCompat implements ModMenuApi {
  @Override
  public String getModId() {
    return "shulkerboxtooltip";
  }

  @Override
  public Function<Screen, ? extends Screen> getConfigScreenFactory() {
    return screen -> null;
    // Creating a config screen crashes the game in the current version of Auto Config.
    // return screen -> AutoConfig.getConfigScreen(Configuration.class, screen).get();
  }
}
