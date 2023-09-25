package com.misterpemodder.shulkerboxtooltip.impl.network.forge;

import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;

public final class ServerNetworkingImpl {
  @SubscribeEvent
  public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    C2SMessages.registerAllFor((ServerPlayerEntity) event.getEntity());
  }

  @SubscribeEvent
  public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
    ServerNetworking.removeClient((ServerPlayerEntity) event.getEntity());
  }

  /**
   * Implements {@link ServerNetworking#createS2CPacket(Identifier, PacketByteBuf)}.
   */
  public static Packet<?> createS2CPacket(Identifier channelId, PacketByteBuf buf) {
    return NetworkDirection.PLAY_TO_CLIENT.buildPacket(buf, channelId).getThis();
  }

  /**
   * Implementation of {@link ServerNetworking#init()}.
   */
  public static void init() {
    C2SMessages.init();
    S2CMessages.init();
    MinecraftForge.EVENT_BUS.register(ServerNetworkingImpl.class);
  }

  /**
   * Implementation of {@link ServerNetworking#registerC2SReceiver(Identifier, ServerPlayerEntity, ServerNetworking.PacketReceiver)}.
   */
  public static void registerC2SReceiver(Identifier channelId, ServerPlayerEntity player,
      ServerNetworking.PacketReceiver receiver) {
    ChannelListener.get(channelId).c2sPacketReceiver = receiver;
  }

  /**
   * Implementation of {@link ServerNetworking#unregisterC2SReceiver(Identifier, ServerPlayerEntity)}.
   */
  public static void unregisterC2SReceiver(Identifier channelId, ServerPlayerEntity player) {
    ChannelListener.get(channelId).c2sPacketReceiver = null;
  }

  /**
   * Implementation of {@link ServerNetworking#addRegistrationChangeListener(Identifier, ServerNetworking.RegistrationChangeListener)}.
   */
  public static void addRegistrationChangeListener(Identifier channelId,
      ServerNetworking.RegistrationChangeListener listener) {
    ChannelListener.get(channelId).c2sRegChangeListener = listener;
  }
}
