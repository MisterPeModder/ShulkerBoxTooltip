package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.hook.EnderChestListenerHolder;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CEnderChestUpdate;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Tracks ender chest inventory changes for server players.
 * <p>
 * MC 26.1 removed ContainerListener/addListener from SimpleContainer.
 * A mixin on PlayerEnderChestContainer.setChanged() triggers the callback.
 */
public final class EnderChestListener {

  private final ServerPlayer player;

  private boolean updateScheduled = false;
  private long lastSentAt = Util.getMillis() - DEBOUNCE_DELAY_MS;
  private static final long DEBOUNCE_DELAY_MS = 1_000;

  private EnderChestListener(ServerPlayer player) {
    this.player = player;
  }

  /**
   * Called from PlayerEnderChestContainerHookMixin when setChanged() fires.
   */
  public void containerChanged() {
    if (!ShulkerBoxTooltipApi.hasModAvailable(this.player)) {
      detachFrom(this.player);
      return;
    }
    if (this.updateScheduled)
      return;

    var server = this.player.level().getServer();
    this.updateScheduled = true;
    long wait = this.lastSentAt + DEBOUNCE_DELAY_MS - Util.getMillis();

    if (wait <= 0) {
      server.schedule(server.wrapRunnable(this::sendUpdate));
    } else {
      CompletableFuture.delayedExecutor(wait, TimeUnit.MILLISECONDS, server::executeIfPossible)
          .execute(this::sendUpdate);
    }
  }

  private void sendUpdate() {
    this.updateScheduled = false;
    if (!ShulkerBoxTooltipApi.hasModAvailable(this.player)) return;

    this.lastSentAt = Util.getMillis();
    S2CMessages.ENDER_CHEST_UPDATE.sendTo(this.player,
        S2CEnderChestUpdate.create(this.player.getEnderChestInventory(), this.player.registryAccess()));
  }

  /**
   * Attempts to attach an ender chest inventory listener to the given player
   * if they don't already have one.
   *
   * @param player The player
   */
  public static void attachTo(ServerPlayer player) {
    var inventory = player == null ? null : player.getEnderChestInventory();

    if (inventory instanceof EnderChestListenerHolder holder && holder.shulkerBoxTooltip$getListener() == null)
      holder.shulkerBoxTooltip$setListener(new EnderChestListener(player));
  }

  /**
   * Attempts to detach an ender chest inventory listener to the given player if they have one.
   *
   * @param player The player
   */
  public static void detachFrom(ServerPlayer player) {
    var inventory = player == null ? null : player.getEnderChestInventory();

    if (inventory instanceof EnderChestListenerHolder holder)
      holder.shulkerBoxTooltip$setListener(null);
  }
}
