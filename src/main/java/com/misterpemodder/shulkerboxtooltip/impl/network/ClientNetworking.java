package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public final class ClientNetworking {
  private static boolean serverAvailable = false;
  private static ProtocolVersion serverProtocolVersion;

  public static void init() {
    if (ShulkerBoxTooltip.config.main.serverIntegration)
      ClientPlayConnectionEvents.INIT.register((handler, client) -> S2CPackets.registerReceivers());
    ClientPlayConnectionEvents.JOIN.register(ClientNetworking::onJoinServer);
  }

  private static void onJoinServer(ClientPlayNetworkHandler handler, PacketSender sender,
      MinecraftClient client) {
    ShulkerBoxTooltip.initPreviewItemsMap();

    if (!ShulkerBoxTooltip.config.main.serverIntegration)
      return;
    ShulkerBoxTooltip.config = Configuration.copyFrom(ShulkerBoxTooltip.savedConfig);
    // Reinit some config values before syncing
    if (!MinecraftClient.getInstance().isIntegratedServerRunning())
      ShulkerBoxTooltip.config.reinitClientSideSyncedValues();

    C2SPackets.startHandshake(sender);
  }

  public static void onHandshakeFinished(MinecraftClient client, ClientPlayNetworkHandler handler,
      PacketByteBuf buf, PacketSender responseSender) {
    ProtocolVersion serverVersion = ProtocolVersion.readFromPacketBuf(buf);

    if (serverVersion != null) {
      if (serverVersion.major == ProtocolVersion.CURRENT.major) {
        serverProtocolVersion = serverVersion;
        serverAvailable = true;
        try {
          ShulkerBoxTooltip.config.readFromPacketBuf(buf);
        } catch (RuntimeException e) {
          ShulkerBoxTooltip.LOGGER.error("failed to read server configuration", e);
        }
        ClientPlayNetworking.unregisterReceiver(S2CPackets.HANDSHAKE_TO_CLIENT);
        return;
      }
      ShulkerBoxTooltip.LOGGER.error("incompatible server protocol version, expected "
          + ProtocolVersion.CURRENT.major + ", got " + serverVersion.major);
    } else {
      ShulkerBoxTooltip.LOGGER.error("could not read server protocol version");
    }
    S2CPackets.unregisterReceivers();
  }

  public static void onEnderChestUpdate(MinecraftClient client, ClientPlayNetworkHandler handler,
      PacketByteBuf buf, PacketSender responseSender) {
    try {
      CompoundTag compound = buf.readCompoundTag();

      if (!compound.contains("inv", NbtType.LIST))
        return;
      ListTag tags = compound.getList("inv", NbtType.COMPOUND);

      client.execute(() -> client.player.getEnderChestInventory().readTags(tags));
    } catch (RuntimeException e) {
      ShulkerBoxTooltip.LOGGER.error("could not read ender chest update packet from server", e);
    }
  }

  public static boolean isServerAvailable() {
    return serverAvailable;
  }

  public static ProtocolVersion serverProtocolVersion() {
    return serverProtocolVersion;
  }
}
