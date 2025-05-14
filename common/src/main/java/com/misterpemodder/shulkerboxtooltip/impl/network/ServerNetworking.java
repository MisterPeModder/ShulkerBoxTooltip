package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.PluginManager;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.S2CChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CEnderChestUpdate;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Server-side network handling.
 */
public class ServerNetworking {
  /**
   * A map of current compatible clients along with their protocol version.
   */
  private static final Map<ServerPlayer, ProtocolVersion> CLIENTS = new WeakHashMap<>();

  private ServerNetworking() {
  }

  /**
   * @param player The player.
   * @return true if the player has the mod installed and server integration turned on.
   */
  public static boolean hasModAvailable(ServerPlayer player) {
    return CLIENTS.containsKey(player);
  }

  /**
   * @param client  The player.
   * @param version The client's protocol version.
   */
  public static void addClient(ServerPlayer client, ProtocolVersion version) {
    CLIENTS.put(client, version);

    // Initialize the providers if not already initialized
    PluginManager.loadProviders();
    Configuration.EnderChestSyncType ecSyncType = ShulkerBoxTooltip.config.server.enderChestSyncType;

    if (ecSyncType != Configuration.EnderChestSyncType.NONE)
      S2CMessages.ENDER_CHEST_UPDATE.sendTo(client,
          S2CEnderChestUpdate.create(client.getEnderChestInventory(), client.registryAccess()));
    if (ecSyncType == Configuration.EnderChestSyncType.ACTIVE)
      EnderChestInventoryListener.attachTo(client);
  }

  public static void removeClient(ServerPlayer client) {
    CLIENTS.remove(client);
    EnderChestInventoryListener.detachFrom(client);
  }

  public static void onPlayerChangeWorld(ServerPlayer player) {
    Configuration.EnderChestSyncType ecSyncType = ShulkerBoxTooltip.config.server.enderChestSyncType;

    if (CLIENTS.containsKey(player) && ecSyncType != Configuration.EnderChestSyncType.NONE) {
      S2CMessages.ENDER_CHEST_UPDATE.sendTo(player,
          S2CEnderChestUpdate.create(player.getEnderChestInventory(), player.registryAccess()));
    }
  }

  /**
   * Performs registration of messages and events.
   */
  @ExpectPlatform
  public static void init() {
    throw new AssertionError("Missing implementation of ServerNetworking.init()");
  }

  /**
   * Creates a server-to-client channel compatible with the current mod platform.
   *
   * @param id   The channel's identifier.
   * @param type The channel's message type.
   * @param <T>  The message type.
   * @return The newly-created channel.
   */
  @ExpectPlatform
  public static <T> S2CChannel<T> createS2CChannel(ResourceLocation id, MessageType<T> type) {
    throw new AssertionError("Missing implementation of Networking.createS2CChannel()");
  }
}
