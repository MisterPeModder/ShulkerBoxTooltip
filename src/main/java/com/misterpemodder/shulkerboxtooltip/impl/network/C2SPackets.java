package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.EnderChestSyncType;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

public final class C2SPackets {
  protected static final Identifier HANDSHAKE_TO_SERVER = ShulkerBoxTooltipUtil.id("c2s_handshake");
  protected static final Identifier ENDER_CHEST_UPDATE_REQUEST =
      ShulkerBoxTooltipUtil.id("ec_update_req");

  protected static void registerReceivers(ServerPlayNetworkHandler handler) {
    ServerPlayNetworking.registerReceiver(handler, HANDSHAKE_TO_SERVER,
        ServerNetworking::onHandshakeAttempt);
    if (ShulkerBoxTooltip.config.server.enderChestSyncType == EnderChestSyncType.PASSIVE)
      ServerPlayNetworking.registerReceiver(handler, ENDER_CHEST_UPDATE_REQUEST,
          ServerNetworking::onEnderChestUpdateRequest);
  }

  protected static void unregisterReceivers(ServerPlayNetworkHandler handler) {
    ServerPlayNetworking.unregisterReceiver(handler, HANDSHAKE_TO_SERVER);
    ServerPlayNetworking.unregisterReceiver(handler, ENDER_CHEST_UPDATE_REQUEST);
  }

  protected static void startHandshake(PacketSender sender) {
    PacketByteBuf buf = PacketByteBufs.create();

    ProtocolVersion.CURRENT.writeToPacketBuf(buf);
    ShulkerBoxTooltip.LOGGER.info(
        "[" + ShulkerBoxTooltip.MOD_NAME + "] Server integration enabled, attempting handshake...");
    sender.sendPacket(HANDSHAKE_TO_SERVER, buf);
  }

  public static void sendEnderChestUpdateRequest(PacketSender sender) {
    if (ClientPlayNetworking.canSend(ENDER_CHEST_UPDATE_REQUEST))
      sender.sendPacket(ENDER_CHEST_UPDATE_REQUEST, PacketByteBufs.empty());
  }
}
