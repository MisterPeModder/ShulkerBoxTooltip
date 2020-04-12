package com.misterpemodder.shulkerboxtooltip.impl.network.client;

import java.util.ArrayList;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ClientConnectionHandler {
  private static boolean serverAvailable = false;
  private static int serverProtocolVersion = 0;

  private static final List<Runnable> ON_CONNECTED_CALLBACKS = new ArrayList<>();

  public static void onJoinServer() {
    ShulkerBoxTooltipClient.initPreviewItemsMap();
    if (C2SPacketTypes.HANDSHAKE_TO_SERVER.canServerReceive())
      C2SPacketTypes.HANDSHAKE_TO_SERVER.sendToServer(ShulkerBoxTooltip.PROTOCOL_VERSION);
  }

  public static void onQuitServer() {
    ON_CONNECTED_CALLBACKS.clear();
  }

  public static void onHandshakeFinished(int serverProtocolVersion) {
    // Run queued callbacks
    if (ON_CONNECTED_CALLBACKS.size() > 0) {
      for (Runnable callback : ON_CONNECTED_CALLBACKS) {
        callback.run();
      }
      ON_CONNECTED_CALLBACKS.clear();
    }
    ClientConnectionHandler.serverProtocolVersion = serverProtocolVersion;
    serverAvailable = true;
  }

  /**
   * Runs the passed callback when the client is connected to a server.
   * If the client is already connected, the callback is executed immediately.
   * 
   * @param callback The callback
   */
  public static void runWhenConnected(Runnable callback) {
    if (serverAvailable)
      callback.run();
    else
      ON_CONNECTED_CALLBACKS.add(callback);
  }

  public static boolean isServerAvailable() {
    return serverAvailable;
  }

  public static int serverProtocolVersion() {
    return serverProtocolVersion;
  }
}
