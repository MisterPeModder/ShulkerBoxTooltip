package com.misterpemodder.shulkerboxtooltip.forge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipComponent;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipData;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public final class ShulkerBoxTooltipClientImpl extends ShulkerBoxTooltipClient {
  public static void init() {
    ShulkerBoxTooltipClient.init();

    // PreviewTooltipData -> PreviewTooltipComponent conversion
    MinecraftForgeClient.registerTooltipComponentFactory(PreviewTooltipData.class,
        PreviewTooltipComponent::new);

    registerConfigScreen();
  }

  private static void registerConfigScreen() {
    ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
        () -> new ConfigGuiHandler.ConfigGuiFactory(
            (client, parent) -> AutoConfig.getConfigScreen(Configuration.class, parent).get()));
  }
}
