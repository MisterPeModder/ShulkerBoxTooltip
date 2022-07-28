package com.misterpemodder.shulkerboxtooltip.impl.network.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.RegistrationChangeType;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class ServerNetworkingImpl {
  private final static Map<Identifier, ServerNetworking.RegistrationChangeListener> REGISTRATION_CHANGE_LISTENERS =
      new HashMap<>();

  /**
   * Implements {@link ServerNetworking#init()}.
   */
  public static void init() {
    if (!ShulkerBoxTooltip.config.server.clientIntegration)
      return;
    S2CMessages.init();
    C2SMessages.init();
    ServerPlayConnectionEvents.INIT.register((handler, server) -> C2SMessages.registerAllFor(handler.player));
    ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> ServerNetworking.removeClient(handler.player));
  }

  /**
   * Implements {@link ServerNetworking#registerC2SReceiver(Identifier, ServerPlayerEntity, ServerNetworking.PacketReceiver)}.
   */
  public static void registerC2SReceiver(Identifier channelId, ServerPlayerEntity player,
      ServerNetworking.PacketReceiver receiver) {
    ServerPlayNetworkHandler handler = player.networkHandler;

    if (handler == null) {
      ShulkerBoxTooltip.LOGGER.error("Cannot register packet receiver for " + channelId + ", player is not in game");
      return;
    }
    ServerPlayNetworking.registerReceiver(handler, channelId,
        (server, player1, handler1, buf, responseSender) -> receiver.handle(player1, buf));
  }

  /**
   * Implements {@link ServerNetworking#unregisterC2SReceiver(Identifier, ServerPlayerEntity)}.
   */
  public static void unregisterC2SReceiver(Identifier channelId, ServerPlayerEntity player) {
    ServerPlayNetworkHandler handler = player.networkHandler;

    if (handler != null) {
      ServerPlayNetworking.unregisterReceiver(handler, channelId);
    }
  }

  /**
   * Implements {@link ServerNetworking#addRegistrationChangeListener(Identifier, ServerNetworking.RegistrationChangeListener)}.
   */
  public static void addRegistrationChangeListener(Identifier channelId,
      ServerNetworking.RegistrationChangeListener listener) {
    REGISTRATION_CHANGE_LISTENERS.put(channelId, listener);
  }

  private static void dispatchRegistrationChangeEvent(Identifier channelId, ServerPlayerEntity sender,
      RegistrationChangeType type) {
    ServerNetworking.RegistrationChangeListener listener = REGISTRATION_CHANGE_LISTENERS.get(channelId);
    if (listener != null)
      listener.onRegistrationChange(sender, type);
  }

  static {
    S2CPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> channels.forEach(
        c -> ServerNetworkingImpl.dispatchRegistrationChangeEvent(c, handler.getPlayer(),
            RegistrationChangeType.REGISTER)));
    S2CPlayChannelEvents.UNREGISTER.register((handler, sender, server, channels) -> channels.forEach(
        c -> ServerNetworkingImpl.dispatchRegistrationChangeEvent(c, handler.getPlayer(),
            RegistrationChangeType.UNREGISTER)));
  }
}
