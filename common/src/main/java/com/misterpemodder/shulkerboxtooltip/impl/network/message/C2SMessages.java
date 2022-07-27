package com.misterpemodder.shulkerboxtooltip.impl.network.message;

import com.misterpemodder.shulkerboxtooltip.impl.network.channel.C2SChannel;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * The client to server messages of ShulkerBoxTooltip.
 */
public final class C2SMessages {
  public static final C2SChannel<C2SHandshakeStart> HANDSHAKE_START = new C2SChannel<>(
      ShulkerBoxTooltipUtil.id("c2s_handshake"), new C2SHandshakeStart.Type());
  public static final C2SChannel<C2SEnderChestUpdateRequest> ENDER_CHEST_UPDATE_REQUEST = new C2SChannel<>(
      ShulkerBoxTooltipUtil.id("ec_update_req"), new C2SEnderChestUpdateRequest.Type());

  /**
   * Guarantees the initialization of the static members.
   */
  public static void init() {
  }

  /**
   * Registers all the client to server messages for the given player.
   *
   * @param player The player.
   */
  public static void registerAllFor(ServerPlayerEntity player) {
    HANDSHAKE_START.registerFor(player);
    ENDER_CHEST_UPDATE_REQUEST.registerFor(player);
  }
}
