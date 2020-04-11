package com.misterpemodder.shulkerboxtooltip.impl.network.server;

public final class S2CPacketTypes {
  public static final S2CHandshakePacketType HANDSHAKE_TO_CLIENT =
      new S2CHandshakePacketType("handshake_to_client");

  public static void register() {
    HANDSHAKE_TO_CLIENT.register();
  }
}
