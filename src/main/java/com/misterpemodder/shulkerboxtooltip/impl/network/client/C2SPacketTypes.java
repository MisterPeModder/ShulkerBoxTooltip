package com.misterpemodder.shulkerboxtooltip.impl.network.client;

public final class C2SPacketTypes {
  public static final C2SHandshakePacketType HANDSHAKE_TO_SERVER =
      new C2SHandshakePacketType("c2s_handshake");
  public static final C2SEnderChestUpdateRequestPacketType ENDER_CHEST_UPDATE_REQUEST =
      new C2SEnderChestUpdateRequestPacketType("ec_update_req");

  public static void register() {
    HANDSHAKE_TO_SERVER.register();
    ENDER_CHEST_UPDATE_REQUEST.register();
  }
}
