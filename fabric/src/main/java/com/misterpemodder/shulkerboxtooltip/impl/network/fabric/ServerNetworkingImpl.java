package com.misterpemodder.shulkerboxtooltip.impl.network.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.EnderChestSyncType;
import com.misterpemodder.shulkerboxtooltip.impl.network.ProtocolVersion;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

public final class ServerNetworkingImpl {
  private static final Set<ServerPlayerEntity> CLIENTS = new HashSet<>();

  /**
   * Implementation of {@link ServerNetworking#init()}.
   */
  public static void init() {
    if (!ShulkerBoxTooltip.config.server.clientIntegration)
      return;
    ServerPlayConnectionEvents.INIT.register(
        (handler, server) -> C2SPacketsImpl.registerReceivers(handler));
    ServerPlayConnectionEvents.DISCONNECT.register(ServerNetworkingImpl::onPlayerDisconnected);
  }

  private static void onPlayerDisconnected(ServerPlayNetworkHandler handler,
      MinecraftServer server) {
    ServerPlayerEntity player = handler.player;
    EnderChestInventoryListener.detachFrom(player);
    CLIENTS.remove(player);
  }

  static void onHandshakeAttempt(MinecraftServer server, ServerPlayerEntity player,
      ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
    ProtocolVersion clientVersion = ProtocolVersion.readFromPacketBuf(buf);

    if (clientVersion == null) {
      ShulkerBoxTooltip.LOGGER.error(
          "[" + ShulkerBoxTooltip.MOD_NAME + "] " + player.getEntityName()
              + ": received invalid handshake packet");
      C2SPacketsImpl.unregisterReceivers(handler);
      return;
    }

    ShulkerBoxTooltip.LOGGER.info(
        "[" + ShulkerBoxTooltip.MOD_NAME + "] " + player.getEntityName() + ": protocol version: "
            + clientVersion);
    S2CPackets.sendHandshakeResponse(sender);
    if (clientVersion.major != ProtocolVersion.CURRENT.major) {
      ShulkerBoxTooltip.LOGGER.error(
          "[" + ShulkerBoxTooltip.MOD_NAME + "] " + player.getEntityName()
              + ": incompatible client protocol version, expected " + ProtocolVersion.CURRENT.major
              + ", got " + clientVersion.major);
      C2SPacketsImpl.unregisterReceivers(handler);
      return;
    }

    CLIENTS.add(player);
    ServerPlayNetworking.unregisterReceiver(handler, C2SPacketsImpl.HANDSHAKE_TO_SERVER);

    ServerEntityEvents.ENTITY_LOAD.register(((entity, world) -> {
      if (entity instanceof ServerPlayerEntity p)
        onPlayerLoad(p);
    }));

    server.execute(() -> onPlayerLoad(player));
  }

  @SuppressWarnings("unused")
  static void onEnderChestUpdateRequest(MinecraftServer server, ServerPlayerEntity player,
      ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
    S2CPackets.sendEnderChestUpdate(sender, player.getEnderChestInventory());
  }

  /**
   * Implementation of {@link ServerNetworking#hasModAvailable(ServerPlayerEntity)}.
   */
  public static boolean hasModAvailable(ServerPlayerEntity player) {
    return CLIENTS.contains(player);
  }

  private static void onPlayerLoad(ServerPlayerEntity player) {
    ServerPlayNetworkHandler handler = player.networkHandler;

    if (handler == null)
      return;
    // Build the preview item map if not present
    ShulkerBoxTooltip.initPreviewItemsMap();
    PacketSender sender = ServerPlayNetworking.getSender(handler);
    EnderChestSyncType ecSyncType = ShulkerBoxTooltip.config.server.enderChestSyncType;

    if (ecSyncType != EnderChestSyncType.NONE)
      S2CPackets.sendEnderChestUpdate(sender, player.getEnderChestInventory());
    if (ecSyncType == EnderChestSyncType.ACTIVE)
      EnderChestInventoryListener.attachTo(player);
  }
}
