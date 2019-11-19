package com.misterpemodder.shulkerboxtooltip.impl;

import java.util.function.Function;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
// import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
// import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuCompat implements ModMenuApi {
  @Override
  public String getModId() {
    return "shulkerboxtooltip";
  }

  @Override
  public Function<Screen, ? extends Screen> getConfigScreenFactory() {
    return screen -> AutoConfig.getConfigScreen(Configuration.class, screen).get();
  }
}
