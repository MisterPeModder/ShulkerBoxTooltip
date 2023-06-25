package com.misterpemodder.shulkerboxtooltip.forge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipComponent;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipData;
import com.mojang.datafixers.util.Either;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
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

      // Register the config screen
      ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
          () -> new ConfigScreenHandler.ConfigScreenFactory(
              (client, parent) -> ConfigurationHandler.ClientOnly.makeConfigScreen(parent)));

      // ItemStack -> PreviewTooltipData
      MinecraftForge.EVENT_BUS.addListener(ShulkerBoxTooltipClientImpl::onGatherTooltipComponents);
    });
  }

  @SubscribeEvent
  public static void onRegisterTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
    // PreviewTooltipData -> PreviewTooltipComponent conversion
    event.register(PreviewTooltipData.class, PreviewTooltipComponent::new);
  }

  private static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
    var context = PreviewContext.of(event.getItemStack(),
        ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player);

    if (ShulkerBoxTooltipApi.isPreviewAvailable(context)) {
      var data = new PreviewTooltipData(ShulkerBoxTooltipApi.getPreviewProviderForStack(context.stack()), context);

      event.getTooltipElements().add(Either.right(data));
    }
  }
}
