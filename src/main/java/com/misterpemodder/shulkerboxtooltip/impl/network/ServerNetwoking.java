package com.misterpemodder.shulkerboxtooltip.impl.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.EnderChestSyncType;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ServerNetwoking {
  private static Set<ServerPlayerEntity> CLIENTS = new HashSet<>();
  private static Map<ServerPlayerEntity, InventoryChangedListener> EC_UPDATE_LISTENERS = new HashMap<>();

  public static void init() {
    if (!ShulkerBoxTooltip.config.server.clientIntegration)
      return;
    ServerPlayConnectionEvents.INIT.register((handler, server) -> C2SPackets.registerReceivers(handler));
    ServerPlayConnectionEvents.DISCONNECT.register(ServerNetwoking::onPlayerDisconnected);
  }

  private static void onPlayerDisconnected(ServerPlayNetworkHandler handler, MinecraftServer server) {
    ServerPlayerEntity player = handler.player;
    InventoryChangedListener listener = EC_UPDATE_LISTENERS.remove(player);

    if (listener != null)
      player.getEnderChestInventory().removeListener(listener);
    CLIENTS.remove(player);
  }

  protected static void onHandshakeAttempt(MinecraftServer server, ServerPlayerEntity player,
      ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
    ProtocolVersion clientVersion = ProtocolVersion.readFromPacketBuf(buf);

    if (clientVersion == null || clientVersion.major != ProtocolVersion.CURRENT.major) {
      ShulkerBoxTooltip.LOGGER.error("recieved invalid handshake packet from player " + player);
      C2SPackets.unregisterReceivers(handler);
      return;
    }
    CLIENTS.add(player);
    ServerPlayNetworking.unregisterReceiver(handler, C2SPackets.HANDSHAKE_TO_SERVER);
    S2CPackets.sendHandshakeResponse(sender);

    // Build the preview item map if not present
    ShulkerBoxTooltip.initPreviewItemsMap();

    // Ender Chest sync
    server.execute(() -> {
      EnderChestSyncType ecSyncType = ShulkerBoxTooltip.config.server.enderChestSyncType;

      if (ecSyncType != EnderChestSyncType.NONE)
        S2CPackets.sendEnderChestUpdate(sender, player.getEnderChestInventory());
      if (ecSyncType == EnderChestSyncType.ACTIVE) {
        InventoryChangedListener listener = inv -> S2CPackets.sendEnderChestUpdate(sender, (EnderChestInventory) inv);

        EC_UPDATE_LISTENERS.put(player, listener);
        player.getEnderChestInventory().addListener(listener);
      }
    });
  }

  protected static void onEnderChestUpdateRequest(MinecraftServer server, ServerPlayerEntity player,
      ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
    S2CPackets.sendEnderChestUpdate(sender, player.getEnderChestInventory());
  }

  public static boolean hasModAvailable(ServerPlayerEntity player) {
    return CLIENTS.contains(player);
  }
}
