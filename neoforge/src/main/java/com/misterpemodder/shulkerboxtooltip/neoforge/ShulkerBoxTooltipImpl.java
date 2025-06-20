package com.misterpemodder.shulkerboxtooltip.neoforge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.neoforge.ShulkerBoxTooltipPlugin;
import com.misterpemodder.shulkerboxtooltip.impl.network.neoforge.ClientNetworkingImpl;
import com.misterpemodder.shulkerboxtooltip.impl.network.neoforge.ServerNetworkingImpl;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.jetbrains.annotations.Contract;

import java.nio.file.Path;


@Mod(ShulkerBoxTooltip.MOD_ID)
@EventBusSubscriber(modid = ShulkerBoxTooltip.MOD_ID)
@SuppressWarnings("unused")
public class ShulkerBoxTooltipImpl extends ShulkerBoxTooltip {
  @SubscribeEvent
  public static void onSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      ModLoadingContext.get().registerExtensionPoint(ShulkerBoxTooltipPlugin.class,
          () -> new ShulkerBoxTooltipPlugin(ShulkerBoxTooltipImpl::new));
      ShulkerBoxTooltip.init();
    });
  }

  @SubscribeEvent
  public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
    ServerNetworkingImpl.S2C_CHANNELS.values().forEach(channel -> channel.registerPayloadTypeDeferred(event));
    ClientNetworkingImpl.C2S_CHANNELS.values().forEach(channel -> channel.registerPayloadTypeDeferred(event));
  }

  /**
   * Implementation of {@link ShulkerBoxTooltip#getConfigDir()}.
   */
  @Contract(value = " -> !null", pure = true)
  public static Path getConfigDir() {
    return FMLPaths.CONFIGDIR.get();
  }
}
