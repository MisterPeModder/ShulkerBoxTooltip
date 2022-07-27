package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CEnderChestUpdate;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
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
    if (!ShulkerBoxTooltipApi.hasModAvailable(this.player)) {
      detachFrom(this.player);
      return;
    }
    S2CMessages.ENDER_CHEST_UPDATE.sendTo(this.player, S2CEnderChestUpdate.create((EnderChestInventory) inv));
  }

  /**
   * Attempts to attach an ender chest inventory listener to the given player
   * if they don't already have one.
   *
   * @param player The player
   */
  public static void attachTo(ServerPlayerEntity player) {
    var inventory = player == null ? null : player.getEnderChestInventory();
    var listeners = inventory == null ? null : inventory.listeners;

    // Search for existing listener
    if (listeners != null) {
      for (InventoryChangedListener listener : listeners)
        if (listener instanceof EnderChestInventoryListener)
          return;
    }
    if (inventory != null)
      inventory.addListener(new EnderChestInventoryListener(player));
  }

  /**
   * Attempts to detach an ender chest inventory listener to the given player if they have one.
   *
   * @param player The player
   */
  public static void detachFrom(ServerPlayerEntity player) {
    var inventory = player == null ? null : player.getEnderChestInventory();
    var listeners = inventory == null ? null : inventory.listeners;

    if (listeners == null)
      return;

    // Search for existing listener and remove it if found
    for (InventoryChangedListener listener : listeners) {
      if (listener instanceof EnderChestInventoryListener) {
        inventory.removeListener(listener);
        return;
      }
    }
  }
}
