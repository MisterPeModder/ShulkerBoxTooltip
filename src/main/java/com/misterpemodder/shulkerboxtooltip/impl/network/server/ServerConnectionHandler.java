package com.misterpemodder.shulkerboxtooltip.impl.network.server;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.impl.network.ProtocolVersion;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerConnectionHandler {
  private WeakReference<ServerPlayerEntity> player;
  private final ProtocolVersion clientProtocolVersion;

  private static final Map<ServerPlayerEntity, ServerConnectionHandler> HANDLERS =
      new WeakHashMap<>();
  private static final Map<ServerPlayerEntity, List<OnConnectedCallback>> ON_CONNECTED_CALLBACKS =
      new WeakHashMap<>();

  protected ServerConnectionHandler(ServerPlayerEntity player,
      ProtocolVersion clientProtocolVersion) {
    this.player = new WeakReference<>(player);
    this.clientProtocolVersion = clientProtocolVersion;

    // Run queued callbacks
    List<OnConnectedCallback> callbacks = ON_CONNECTED_CALLBACKS.remove(player);
    if (callbacks != null) {
      for (OnConnectedCallback callback : callbacks) {
        callback.apply(this, player);
      }
    }
  }

  @Nullable
  public ServerPlayerEntity getPlayer() {
    return this.player.get();
  }

  @Nullable
  public static ServerConnectionHandler getPlayerConnection(ServerPlayerEntity player) {
    return HANDLERS.get(player);
  }

  public ProtocolVersion getClientProtocolVersion() {
    return clientProtocolVersion;
  }

  public static void runWhenConnected(ServerPlayerEntity player, OnConnectedCallback callback) {
    List<OnConnectedCallback> callbacks = ON_CONNECTED_CALLBACKS.get(player);

    if (callbacks == null) {
      callbacks = new ArrayList<>();
      ON_CONNECTED_CALLBACKS.put(player, callbacks);
    }
    callbacks.add(callback);
  }

  public static void onHandshakeAttempt(ServerPlayerEntity player,
      ProtocolVersion clientProtocolVersion) {
    S2CPacketTypes.HANDSHAKE_TO_CLIENT.sendToPlayer(player, ProtocolVersion.CURRENT);
    HANDLERS.put(player, new ServerConnectionHandler(player, clientProtocolVersion));
  }

  public static void onPlayerConnect(ServerPlayerEntity p) {
    runWhenConnected(p, (handler, player) -> {
      if (handler.clientProtocolVersion.major != 1)
        return;

      // Ender Chest sync
      S2CPacketTypes.ENDER_CHEST_UPDATE.sendToPlayer(player, player.getEnderChestInventory());
      player.getEnderChestInventory().addListener(inv -> {
        S2CPacketTypes.ENDER_CHEST_UPDATE.sendToPlayer(player, (EnderChestInventory) inv);
      });
    });
  }

  @FunctionalInterface
  public interface OnConnectedCallback {
    void apply(ServerConnectionHandler handler, ServerPlayerEntity player);
  }
}
