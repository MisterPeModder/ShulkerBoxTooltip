package com.misterpemodder.shulkerboxtooltip.impl.network.client;

public final class C2SPacketTypes {
  public static final C2SHandshakePacketType HANDSHAKE_TO_SERVER =
      new C2SHandshakePacketType("handshake_to_server");

  public static void register() {
    HANDSHAKE_TO_SERVER.register();
  }
}
