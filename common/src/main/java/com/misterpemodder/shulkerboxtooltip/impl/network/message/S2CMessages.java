package com.misterpemodder.shulkerboxtooltip.impl.network.message;

import com.misterpemodder.shulkerboxtooltip.impl.network.channel.S2CChannel;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The server to client messages of ShulkerBoxTooltip.
 */
public class S2CMessages {
  public static final S2CChannel<S2CHandshakeResponse> HANDSHAKE_RESPONSE = new S2CChannel<>(
      ShulkerBoxTooltipUtil.id("s2c_handshake"), new S2CHandshakeResponse.Type());
  public static final S2CChannel<S2CEnderChestUpdate> ENDER_CHEST_UPDATE = new S2CChannel<>(
      ShulkerBoxTooltipUtil.id("ec_update"), new S2CEnderChestUpdate.Type());

  /**
   * Guarantees the initialization of the static members.
   */
  public static void init() {
  }

  /**
   * Registers all to server to client messages.
   */
  @Environment(EnvType.CLIENT)
  public static void registerAll() {
    HANDSHAKE_RESPONSE.register();
    ENDER_CHEST_UPDATE.register();
  }

  /**
   * Unregisters all to server to client messages.
   */
  @Environment(EnvType.CLIENT)
  public static void unregisterAll() {
    HANDSHAKE_RESPONSE.unregister();
    ENDER_CHEST_UPDATE.unregister();
  }
}
