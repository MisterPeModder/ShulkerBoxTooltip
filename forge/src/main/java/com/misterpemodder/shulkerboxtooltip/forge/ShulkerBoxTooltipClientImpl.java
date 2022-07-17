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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ShulkerBoxTooltip.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ShulkerBoxTooltipClientImpl extends ShulkerBoxTooltipClient {
  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      ShulkerBoxTooltipClient.init();

      // PreviewTooltipData -> PreviewTooltipComponent conversion
      MinecraftForgeClient.registerTooltipComponentFactory(PreviewTooltipData.class,
          PreviewTooltipComponent::new);

      // Register the config screen
      ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
          () -> new ConfigGuiHandler.ConfigGuiFactory(
              (client, parent) -> AutoConfig.getConfigScreen(Configuration.class, parent).get()));
    });
  }
}
