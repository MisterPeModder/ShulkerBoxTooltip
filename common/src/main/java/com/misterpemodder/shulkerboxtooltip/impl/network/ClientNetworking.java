package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.PluginManager;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.C2SChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import com.misterpemodder.shulkerboxtooltip.impl.provider.EnderChestCache;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;

/**
 * Client-side network handling.
 */
public class ClientNetworking {
  /**
   * The server's network protocol version, null when not connected or the server is not compatible.
   */
  @Nullable
  @Environment(EnvType.CLIENT)
  public static ProtocolVersion serverProtocolVersion;

  private ClientNetworking() {
  }

  /**
   * Corresponds to Fabric's <code>ClientPlayConnectionEvents.JOIN</code> and
   * Forge's <code>ClientPlayerNetworkEvent.LoggedInEvent</code> events.
   */
  @Environment(EnvType.CLIENT)
  public static void onJoinServer(Minecraft client) {
    client.execute(() -> {
      PluginManager.loadColors();
      PluginManager.loadProviders();
    });
    ShulkerBoxTooltip.configTree.copy(ShulkerBoxTooltip.savedConfig, ShulkerBoxTooltip.config);

    // Re-init some config values before syncing
    serverProtocolVersion = null;
    if (!Minecraft.getInstance().hasSingleplayerServer())
      ConfigurationHandler.reinitClientSideSyncedValues(ShulkerBoxTooltip.config);
    C2SMessages.attemptHandshake();

    // Load the ender chest cache for this world/server
    EnderChestCache.INSTANCE.loadFromDisk();
  }

  /**
   * Performs registration of messages and events.
   */
  @ExpectPlatform
  @Environment(EnvType.CLIENT)
  public static void init() {
    throw new AssertionError("Missing implementation of ClientNetworking.init()");
  }

  /**
   * Creates a client-to-server channel compatible with the current mod platform.
   *
   * @param id   The channel's identifier.
   * @param type The channel's message type.
   * @param <T>  The message type.
   * @return The newly-created channel.
   */
  @ExpectPlatform
  public static <T> C2SChannel<T> createC2SChannel(Identifier id, MessageType<T> type) {
    throw new AssertionError("Missing implementation of Networking.createC2SChannel()");
  }
}
