package com.misterpemodder.shulkerboxtooltip.impl.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.server.network.ServerPlayerEntity;

public final class EnderChestInventoryListener implements InventoryChangedListener {

  private final ServerPlayerEntity player;

  private EnderChestInventoryListener(ServerPlayerEntity player) {
    this.player = player;
  }

  public void onInventoryChanged(Inventory inv) {
    PacketSender sender = ServerPlayNetworking.getSender(this.player);

    S2CPackets.sendEnderChestUpdate(sender, (EnderChestInventory) inv);
  }

  /**
   * Attempts to attach an ender chest inventory listener to the given player
   * if they don't already have one.
   *
   * @param player The player
   */
  public static void attachTo(ServerPlayerEntity player) {
    var listeners = player.getEnderChestInventory().listeners;

    // Search for existing listener
    if (listeners != null) {
      for (InventoryChangedListener listener : listeners)
        if (listener instanceof EnderChestInventoryListener)
          return;
    }
    player.getEnderChestInventory().addListener(new EnderChestInventoryListener(player));
  }

  /**
   * Attempts to detach an ender chest inventory listener to the given player if they have one.
   *
   * @param player The player
   */
  public static void detachFrom(ServerPlayerEntity player) {
    var listeners = player.getEnderChestInventory().listeners;

    if (listeners == null)
      return;

    // Search for existing listener and remove it if found
    for (InventoryChangedListener listener : listeners) {
      if (listener instanceof EnderChestInventoryListener) {
        player.getEnderChestInventory().removeListener(listener);
        return;
      }
    }
  }
}
