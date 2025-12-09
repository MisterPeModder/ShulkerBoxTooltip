package com.misterpemodder.shulkerboxtooltip.impl.network.neoforge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.S2CChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;

public final class ServerNetworkingImpl {
  public static final Map<Identifier, NeoForgeS2CChannel<?>> S2C_CHANNELS = new HashMap<>();

  private ServerNetworkingImpl() {
  }

  @SubscribeEvent
  public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    C2SMessages.registerAllFor((ServerPlayer) event.getEntity());
  }

  @SubscribeEvent
  public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
    ServerNetworking.removeClient((ServerPlayer) event.getEntity());
  }

  @SubscribeEvent
  public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
    ServerNetworking.onPlayerChangeWorld((ServerPlayer) event.getEntity());
  }

  /**
   * Implementation of {@link ServerNetworking#init()}.
   */
  public static void init() {
    if (!ShulkerBoxTooltip.config.server.clientIntegration)
      return;
    S2CMessages.registerPayloadTypes();
    C2SMessages.registerPayloadTypes();
    NeoForge.EVENT_BUS.register(ServerNetworkingImpl.class);
  }

  /**
   * Implements {@link ServerNetworking#createS2CChannel(Identifier, MessageType)}.
   */
  public static <T> S2CChannel<T> createS2CChannel(Identifier id, MessageType<T> type) {
    var channel = new NeoForgeS2CChannel<>(id, type);
    S2C_CHANNELS.put(id, channel);
    return channel;
  }
}
