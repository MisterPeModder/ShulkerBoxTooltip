package com.misterpemodder.shulkerboxtooltip.impl.network.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.S2CChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.C2SMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public final class ServerNetworkingImpl {
  private static final Map<Identifier, FabricS2CChannel<?>> S2C_CHANNELS = new HashMap<>();

  private ServerNetworkingImpl() {
  }

  /**
   * Implements {@link ServerNetworking#init()}.
   */
  public static void init() {
    if (!ShulkerBoxTooltip.config.server.clientIntegration)
      return;
    S2CMessages.registerPayloadTypes();
    C2SMessages.registerPayloadTypes();
    ServerPlayConnectionEvents.INIT.register((handler, server) -> C2SMessages.registerAllFor(handler.player));
    ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> ServerNetworking.removeClient(handler.player));
    S2CPlayChannelEvents.REGISTER.register(
        (handler, sender, server, ids) -> ids.forEach(id -> onRegisterChannel(id, handler.getPlayer())));
    S2CPlayChannelEvents.UNREGISTER.register(
        (handler, sender, server, ids) -> ids.forEach(id -> onUnregisterChannel(id, handler.getPlayer())));
    ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(
        (player, origin, destination) -> ServerNetworking.onPlayerChangeWorld(player));
  }

  /**
   * Implements {@link ServerNetworking#createS2CChannel(Identifier, MessageType)}.
   */
  public static <T> S2CChannel<T> createS2CChannel(Identifier id, MessageType<T> type) {
    var channel = new FabricS2CChannel<>(id, type);
    S2C_CHANNELS.put(id, channel);
    return channel;
  }

  @SuppressWarnings("unchecked")
  private static <T> void onRegisterChannel(Identifier id, ServerPlayer player) {
    FabricS2CChannel<T> channel = (FabricS2CChannel<T>) S2C_CHANNELS.get(id);
    if (channel != null)
      channel.onRegister(new C2SMessageContext<>(player, channel));
  }

  @SuppressWarnings("unchecked")
  private static <T> void onUnregisterChannel(Identifier id, ServerPlayer player) {
    FabricS2CChannel<T> channel = (FabricS2CChannel<T>) S2C_CHANNELS.get(id);
    if (channel != null)
      channel.onUnregister(new C2SMessageContext<>(player, channel));
  }
}
