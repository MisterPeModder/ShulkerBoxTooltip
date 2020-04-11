package com.misterpemodder.shulkerboxtooltip.impl.network.server;

public final class S2CPacketTypes {
  public static final S2CHandshakePacketType HANDSHAKE_TO_CLIENT =
      new S2CHandshakePacketType("handshake_to_client");
  public static final S2CEnderChestUpdatePacketType ENDER_CHEST_UPDATE =
      new S2CEnderChestUpdatePacketType("ender_chest_update");

  public static void register() {
    HANDSHAKE_TO_CLIENT.register();
    ENDER_CHEST_UPDATE.register();
  }
}
