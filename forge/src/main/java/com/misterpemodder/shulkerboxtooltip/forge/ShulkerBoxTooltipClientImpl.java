package com.misterpemodder.shulkerboxtooltip.forge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.ModLoadingContext;

@OnlyIn(Dist.CLIENT)
public final class ShulkerBoxTooltipClientImpl extends ShulkerBoxTooltipClient {
  public static void init() {
    new ShulkerBoxTooltipClientImpl().onInitializeClient();
    registerConfigScreen();
  }

  private static void registerConfigScreen() {
    ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
        () -> new ConfigGuiHandler.ConfigGuiFactory(
            (client, parent) -> AutoConfig.getConfigScreen(Configuration.class, parent).get()));
  }
}
