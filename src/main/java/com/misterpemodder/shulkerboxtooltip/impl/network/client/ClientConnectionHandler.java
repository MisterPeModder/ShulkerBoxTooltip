package com.misterpemodder.shulkerboxtooltip.impl.network.client;

import java.util.ArrayList;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.network.ProtocolVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public final class ClientConnectionHandler {
  private static boolean serverAvailable = false;
  private static ProtocolVersion serverProtocolVersion;

  private static final List<Runnable> ON_CONNECTED_CALLBACKS = new ArrayList<>();

  public static void onJoinServer() {
    ShulkerBoxTooltip.initPreviewItemsMap();

    ShulkerBoxTooltip.config = Configuration.copyFrom(ShulkerBoxTooltip.savedConfig);
    // Reinit some config value before syncing
    if (!MinecraftClient.getInstance().isIntegratedServerRunning())
      ShulkerBoxTooltip.config.reinitClientSideSyncedValues();

    if (ShulkerBoxTooltip.config.main.serverIntegration && C2SPacketTypes.HANDSHAKE_TO_SERVER.canServerReceive())
      C2SPacketTypes.HANDSHAKE_TO_SERVER.sendToServer(ProtocolVersion.CURRENT);
  }

  public static void onQuitServer() {
    ON_CONNECTED_CALLBACKS.clear();
  }

  public static void onHandshakeFinished(ProtocolVersion serverProtocolVersion) {
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

  public static ProtocolVersion serverProtocolVersion() {
    return serverProtocolVersion;
  }
}
