package com.misterpemodder.shulkerboxtooltip.impl.network.neoforge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.C2SChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.HashMap;
import java.util.Map;

public final class ClientNetworkingImpl {
  public static final Map<Identifier, NeoForgeC2SChannel<?>> C2S_CHANNELS = new HashMap<>();

  private ClientNetworkingImpl() {
  }

  @SubscribeEvent
  public static void onJoinServer(ClientPlayerNetworkEvent.LoggingIn event) {
    if (ShulkerBoxTooltip.config.preview.serverIntegration)
      S2CMessages.registerAll();
    ClientNetworking.onJoinServer(Minecraft.getInstance());
  }

  @SubscribeEvent
  public static void onLeaveServer(ClientPlayerNetworkEvent.LoggingOut event) {
    if (ShulkerBoxTooltip.config.preview.serverIntegration)
      C2SMessages.onDisconnectFromServer();
  }

  /**
   * Implements {@link ClientNetworking#init()}.
   */
  public static void init() {
    S2CMessages.registerPayloadTypes();
    C2SMessages.registerPayloadTypes();
    NeoForge.EVENT_BUS.register(ClientNetworkingImpl.class);
  }

  /**
   * Implements {@link ClientNetworking#createC2SChannel(Identifier, MessageType)}.
   */
  public static <T> C2SChannel<T> createC2SChannel(Identifier id, MessageType<T> type) {
    var channel = new NeoForgeC2SChannel<>(id, type);
    C2S_CHANNELS.put(id, channel);
    return channel;
  }
}
