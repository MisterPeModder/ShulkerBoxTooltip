package com.misterpemodder.shulkerboxtooltip.neoforge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ConfigScreen;
import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewClientTooltipComponent;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipComponent;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(value = Dist.CLIENT, modid = ShulkerBoxTooltip.MOD_ID)
public final class ShulkerBoxTooltipClientImpl extends ShulkerBoxTooltipClient {
  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      ShulkerBoxTooltipClient.init();

      // Register the config screen
      ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
          () -> (_, parent) -> new ConfigScreen<>(parent, ShulkerBoxTooltip.configTree, ShulkerBoxTooltip.savedConfig,
              ConfigurationHandler::saveToFile));
    });
  }

  @SubscribeEvent
  public static void onRegisterTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
    // PreviewTooltipComponent -> PreviewClientTooltipComponent conversion
    event.register(PreviewTooltipComponent.class, PreviewClientTooltipComponent::new);
  }

  @SubscribeEvent
  private static void onRenderTooltipTexture(RenderTooltipEvent.Texture event) {
    var extendedGraphics = (GuiGraphicsExtensions) event.getGraphics();
    extendedGraphics.setTooltipTopXPosition(event.getX());
    extendedGraphics.setTooltipTopYPosition(event.getY());
  }

  @SubscribeEvent
  private static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
    var context = PreviewContext.builder(event.getItemStack()).withOwner(
        ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player).build();
    var elements = event.getTooltipElements();

    // Add the preview window at the beginning of the tooltip
    if (ShulkerBoxTooltipApi.isPreviewAvailable(context)) {
      // NeoForge did not remove the vanilla rendered bundle tooltip
      elements.removeIf(either -> either.right().map(c -> c instanceof BundleTooltip).orElse(false));

      var data = new PreviewTooltipComponent(
          ShulkerBoxTooltipApi.getPreviewProviderForStackWithOverrides(context.stack()), context);

      elements.add(1, Either.right(data));
    }

    // Add the tooltip hints at the end of the tooltip
    ShulkerBoxTooltipClient.modifyStackTooltip(context.stack(),
        toAdd -> toAdd.stream().map(Either::<FormattedText, TooltipComponent>left).forEach(elements::add));
  }
}
