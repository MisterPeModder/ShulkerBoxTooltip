package com.misterpemodder.shulkerboxtooltip.impl.network.forge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ShulkerBoxTooltip.MOD_ID)
public final class ClientNetworkingImpl extends ClientNetworking {
  @SubscribeEvent
  public static void onJoinServer(ClientPlayerNetworkEvent.LoggedInEvent event) {
    ClientNetworking.onJoinServer(MinecraftClient.getInstance());
  }

  /**
   * Implements {@link ClientNetworking#init()}.
   */
  public static void init() {
    // TODO: implement networking on forge
  }
}
