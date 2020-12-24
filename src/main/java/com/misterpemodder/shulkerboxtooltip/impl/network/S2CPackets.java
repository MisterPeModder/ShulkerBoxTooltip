package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.hook.EnderChestInventoryPrevTagAccessor;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class S2CPackets {
  protected static final Identifier HANDSHAKE_TO_CLIENT = ShulkerBoxTooltipUtil.identifier("s2c_handshake");
  protected static final Identifier ENDER_CHEST_UPDATE = ShulkerBoxTooltipUtil.identifier("ec_update");

  protected static void registerReceivers() {
    ClientPlayNetworking.registerReceiver(HANDSHAKE_TO_CLIENT, ClientNetworking::onHandshakeFinished);
    ClientPlayNetworking.registerReceiver(ENDER_CHEST_UPDATE, ClientNetworking::onEnderChestUpdate);
  }

  protected static void unregisterReceivers() {
    ClientPlayNetworking.unregisterReceiver(HANDSHAKE_TO_CLIENT);
    ClientPlayNetworking.unregisterReceiver(ENDER_CHEST_UPDATE);
  }

  protected static void sendHandshakeResponse(PacketSender sender) {
    PacketByteBuf buf = PacketByteBufs.create();

    ProtocolVersion.CURRENT.writeToPacketBuf(buf);
    ShulkerBoxTooltip.config.writeToPacketBuf(buf);
    sender.sendPacket(HANDSHAKE_TO_CLIENT, buf);
  }

  protected static void sendEnderChestUpdate(PacketSender sender, EnderChestInventory inventory) {
    PacketByteBuf buf = PacketByteBufs.create();
    CompoundTag compound = new CompoundTag();
    ListTag previous = ((EnderChestInventoryPrevTagAccessor) inventory).shulkerboxtooltip$getPrevTags();
    ListTag current = inventory.getTags();

    // Check if the inventory has been modified
    if (current.equals(previous))
      return;
    ((EnderChestInventoryPrevTagAccessor) inventory).shulkerboxtooltip$setPrevTags(current);
    compound.put("inv", current);
    buf.writeCompoundTag(compound);
    sender.sendPacket(S2CPackets.ENDER_CHEST_UPDATE, buf);
  }
}
