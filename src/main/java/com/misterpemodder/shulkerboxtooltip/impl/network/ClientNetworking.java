package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class ClientNetworking {
  @Nullable
  private static ProtocolVersion serverProtocolVersion;

  public static void init() {
    if (ShulkerBoxTooltip.config.preview.serverIntegration)
      ClientPlayConnectionEvents.INIT.register((handler, client) -> S2CPackets.registerReceivers());
    ClientPlayConnectionEvents.JOIN.register(ClientNetworking::onJoinServer);
    C2SPlayChannelEvents.REGISTER.register(ClientNetworking::onChannelRegister);
  }

  private static void onJoinServer(ClientPlayNetworkHandler handler, PacketSender sender,
      MinecraftClient client) {
    client.execute(ShulkerBoxTooltip::initPreviewItemsMap);
    ShulkerBoxTooltip.config = ConfigurationHandler.copyOf(ShulkerBoxTooltip.savedConfig);

    // Re-init some config values before syncing
    serverProtocolVersion = null;
    if (!MinecraftClient.getInstance().isIntegratedServerRunning())
      ConfigurationHandler.reinitClientSideSyncedValues(ShulkerBoxTooltip.config);
  }

  private static void onChannelRegister(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client, List<Identifier> channels) {
    if (ShulkerBoxTooltip.config.preview.serverIntegration && serverProtocolVersion == null && channels.contains(C2SPackets.HANDSHAKE_TO_SERVER)) {
      C2SPackets.startHandshake(sender);
    }
  }

  @SuppressWarnings("unused")
  public static void onHandshakeFinished(MinecraftClient client, ClientPlayNetworkHandler handler,
      PacketByteBuf buf, PacketSender responseSender) {
    ProtocolVersion serverVersion = ProtocolVersion.readFromPacketBuf(buf);

    ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME + "] Handshake succeeded");
    if (serverVersion != null) {
      if (serverVersion.major == ProtocolVersion.CURRENT.major) {
        ShulkerBoxTooltip.LOGGER
            .info("[" + ShulkerBoxTooltip.MOD_NAME + "] Server protocol version: " + serverVersion);

        serverProtocolVersion = serverVersion;
        try {
          ConfigurationHandler.readFromPacketBuf(ShulkerBoxTooltip.config, buf);
        } catch (RuntimeException e) {
          ShulkerBoxTooltip.LOGGER.error("failed to read server configuration", e);
        }
        ClientPlayNetworking.unregisterReceiver(S2CPackets.HANDSHAKE_TO_CLIENT);
        return;
      }
      ShulkerBoxTooltip.LOGGER.error(
          "[" + ShulkerBoxTooltip.MOD_NAME + "] Incompatible server protocol version, expected "
              + ProtocolVersion.CURRENT.major + ", got " + serverVersion.major);
    } else {
      ShulkerBoxTooltip.LOGGER
          .error("[" + ShulkerBoxTooltip.MOD_NAME + "] Could not read server protocol version");
    }
    S2CPackets.unregisterReceivers();
  }

  @SuppressWarnings("unused")
  public static void onEnderChestUpdate(MinecraftClient client, ClientPlayNetworkHandler handler,
      PacketByteBuf buf, PacketSender responseSender) {
    try {
      NbtCompound compound = buf.readNbt();

      if (compound == null || !compound.contains("inv", NbtType.LIST))
        return;
      NbtList tags = compound.getList("inv", NbtType.COMPOUND);

      client.execute(() -> client.player.getEnderChestInventory().readNbtList(tags));
    } catch (RuntimeException e) {
      ShulkerBoxTooltip.LOGGER.error("could not read ender chest update packet from server", e);
    }
  }

  public static ProtocolVersion serverProtocolVersion() {
    return serverProtocolVersion;
  }
}
