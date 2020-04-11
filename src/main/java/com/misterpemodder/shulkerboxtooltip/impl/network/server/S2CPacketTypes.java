package com.misterpemodder.shulkerboxtooltip.impl.network.server;

public final class S2CPacketTypes {
  public static final S2CPacketType SERVER_AVAILABLE =
      new S2CServerAvailablePacketType("server_available");

  public static void register() {
    SERVER_AVAILABLE.register();
  }
}
