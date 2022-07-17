package com.misterpemodder.shulkerboxtooltip.impl.network.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import com.misterpemodder.shulkerboxtooltip.impl.network.ProtocolVersion;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class S2CPackets {
  static final Identifier HANDSHAKE_TO_CLIENT = ShulkerBoxTooltipUtil.id("s2c_handshake");
  static final Identifier ENDER_CHEST_UPDATE = ShulkerBoxTooltipUtil.id("ec_update");

  static void registerReceivers() {
    ClientPlayNetworking.registerReceiver(HANDSHAKE_TO_CLIENT,
        ClientNetworkingImpl::onHandshakeFinished);
    ClientPlayNetworking.registerReceiver(ENDER_CHEST_UPDATE, ClientNetworkingImpl::onEnderChestUpdate);
  }

  static void unregisterReceivers() {
    ClientPlayNetworking.unregisterReceiver(HANDSHAKE_TO_CLIENT);
    ClientPlayNetworking.unregisterReceiver(ENDER_CHEST_UPDATE);
  }

  static void sendHandshakeResponse(PacketSender sender) {
    PacketByteBuf buf = PacketByteBufs.create();

    ProtocolVersion.CURRENT.writeToPacketBuf(buf);
    ConfigurationHandler.writeToPacketBuf(ShulkerBoxTooltip.config, buf);
    sender.sendPacket(HANDSHAKE_TO_CLIENT, buf);
  }

  static void sendEnderChestUpdate(PacketSender sender, EnderChestInventory inventory) {
    PacketByteBuf buf = PacketByteBufs.create();
    NbtCompound compound = new NbtCompound();

    compound.put("inv", inventory.toNbtList());
    buf.writeNbt(compound);
    sender.sendPacket(S2CPackets.ENDER_CHEST_UPDATE, buf);
  }
}
