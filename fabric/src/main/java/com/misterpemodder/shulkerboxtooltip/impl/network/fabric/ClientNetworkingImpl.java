package com.misterpemodder.shulkerboxtooltip.impl.network.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.C2SChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.S2CMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ServerboundPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class ClientNetworkingImpl {
  private static final Map<Identifier, FabricC2SChannel<?>> C2S_CHANNELS = new HashMap<>();

  private ClientNetworkingImpl() {
  }

  /**
   * Implements {@link ClientNetworking#init()}.
   */
  @Environment(EnvType.CLIENT)
  public static void init() {
    if (ShulkerBoxTooltip.config.preview.serverIntegration) {
      S2CMessages.registerPayloadTypes();
      C2SMessages.registerPayloadTypes();
      ClientPlayConnectionEvents.INIT.register((handler, client) -> S2CMessages.registerAll());
      ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> C2SMessages.onDisconnectFromServer());

      ServerboundPlayChannelEvents.REGISTER.register(
          (handler, sender, server, ids) -> ids.forEach(ClientNetworkingImpl::onRegisterChannel));
      ServerboundPlayChannelEvents.UNREGISTER.register(
          (handler, sender, server, ids) -> ids.forEach(ClientNetworkingImpl::onUnregisterChannel));
    }
    ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientNetworking.onJoinServer(client));
  }

  /**
   * Implements {@link ClientNetworking#createC2SChannel(Identifier, MessageType)}.
   */
  public static <T> C2SChannel<T> createC2SChannel(Identifier id, MessageType<T> type) {
    var channel = new FabricC2SChannel<>(id, type);
    C2S_CHANNELS.put(id, channel);
    return channel;
  }

  @Environment(EnvType.CLIENT)
  @SuppressWarnings("unchecked")
  private static <T> void onRegisterChannel(Identifier id) {
    FabricC2SChannel<T> channel = (FabricC2SChannel<T>) C2S_CHANNELS.get(id);
    if (channel != null)
      channel.onRegister(new S2CMessageContext<>(channel));
  }

  @Environment(EnvType.CLIENT)
  @SuppressWarnings("unchecked")
  private static <T> void onUnregisterChannel(Identifier id) {
    FabricC2SChannel<T> channel = (FabricC2SChannel<T>) C2S_CHANNELS.get(id);
    if (channel != null)
      channel.onUnregister(new S2CMessageContext<>(channel));
  }
}
