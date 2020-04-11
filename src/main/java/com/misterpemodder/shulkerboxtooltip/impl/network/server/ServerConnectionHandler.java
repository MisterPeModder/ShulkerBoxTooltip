package com.misterpemodder.shulkerboxtooltip.impl.network.server;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ServerConnectionHandler {
  public static void onHandshakeAttempt(ServerPlayerEntity player) {
    S2CPacketTypes.HANDSHAKE_TO_CLIENT.sendToPlayer(player, ShulkerBoxTooltip.PROTOCOL_VERSION);
  }

  public static void onPlayerConnect(ServerPlayerEntity player) {
    // Ender Chest sync
    S2CPacketTypes.ENDER_CHEST_UPDATE.sendToPlayer(player, player.getEnderChestInventory());
    player.getEnderChestInventory().addListener(inv -> {
      S2CPacketTypes.ENDER_CHEST_UPDATE.sendToPlayer(player, (EnderChestInventory) inv);
    });
  }
}
