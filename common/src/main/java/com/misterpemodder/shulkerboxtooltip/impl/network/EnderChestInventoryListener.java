package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CEnderChestUpdate;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.PlayerEnderChestContainer;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Tracks ender chest inventory changes for server players.
 * <p>
 * MC 26.1 removed ContainerListener/addListener from SimpleContainer.
 * This class now tracks listeners via a static map; a mixin on
 * SimpleContainer.setChanged() triggers the callback.
 */
public final class EnderChestInventoryListener {

  private static final Map<PlayerEnderChestContainer, EnderChestInventoryListener> LISTENERS = new WeakHashMap<>();

  private final ServerPlayer player;

  private EnderChestInventoryListener(ServerPlayer player) {
    this.player = player;
  }

  public void containerChanged(Container inv) {
    if (!ShulkerBoxTooltipApi.hasModAvailable(this.player)) {
      detachFrom(this.player);
      return;
    }
    S2CMessages.ENDER_CHEST_UPDATE.sendTo(this.player,
        S2CEnderChestUpdate.create((PlayerEnderChestContainer) inv, this.player.registryAccess()));
  }

  /**
   * Called from SimpleContainerMixin when setChanged() fires on a PlayerEnderChestContainer.
   */
  public static void onContainerChanged(Container container) {
    if (container instanceof PlayerEnderChestContainer enderChest) {
      var listener = LISTENERS.get(enderChest);
      if (listener != null) {
        listener.containerChanged(container);
      }
    }
  }

  /**
   * Attempts to attach an ender chest inventory listener to the given player
   * if they don't already have one.
   *
   * @param player The player
   */
  public static void attachTo(ServerPlayer player) {
    var inventory = player == null ? null : player.getEnderChestInventory();
    if (inventory == null) return;

    LISTENERS.putIfAbsent(inventory, new EnderChestInventoryListener(player));
  }

  /**
   * Attempts to detach an ender chest inventory listener to the given player if they have one.
   *
   * @param player The player
   */
  public static void detachFrom(ServerPlayer player) {
    var inventory = player == null ? null : player.getEnderChestInventory();
    if (inventory == null) return;

    LISTENERS.remove(inventory);
  }
}
