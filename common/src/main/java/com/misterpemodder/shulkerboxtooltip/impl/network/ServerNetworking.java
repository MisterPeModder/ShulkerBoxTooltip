package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.PluginManager;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CEnderChestUpdate;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Server-side network handling.
 */
public class ServerNetworking {
  /**
   * A map of current compatible clients along with their protocol version.
   */
  private static final Map<ServerPlayerEntity, ProtocolVersion> CLIENTS = new WeakHashMap<>();

  /**
   * @param player The player.
   * @return true if the player has the mod installed and server integration turned on.
   */
  public static boolean hasModAvailable(ServerPlayerEntity player) {
    return CLIENTS.containsKey(player);
  }

  /**
   * @param client The player.
   * @param version The client's protocol version.
   */
  public static void addClient(ServerPlayerEntity client, ProtocolVersion version) {
    CLIENTS.put(client, version);

    // Initialize the providers if not already initialized
    PluginManager.loadProviders();
    Configuration.EnderChestSyncType ecSyncType = ShulkerBoxTooltip.config.server.enderChestSyncType;

    if (ecSyncType != Configuration.EnderChestSyncType.NONE)
      S2CMessages.ENDER_CHEST_UPDATE.sendTo(client, S2CEnderChestUpdate.create(client.getEnderChestInventory()));
    if (ecSyncType == Configuration.EnderChestSyncType.ACTIVE)
      EnderChestInventoryListener.attachTo(client);
  }

  public static void removeClient(ServerPlayerEntity client) {
    CLIENTS.remove(client);
    EnderChestInventoryListener.detachFrom(client);
  }

  /**
   * Creates a vanilla custom payload packet from the given channel identifier and raw data.
   *
   * @param channelId The channel identifier.
   * @param buf       The packet's data.
   * @return A custom vanilla packet.
   */
  public static Packet<?> createS2CPacket(Identifier channelId, PacketByteBuf buf) {
    return new CustomPayloadS2CPacket(channelId, buf);
  }

  /**
   * Performs registration of messages and events.
   */
  @ExpectPlatform
  public static void init() {
    throw new AssertionError("Missing implementation of ServerNetworking.init()");
  }

  /**
   * Registers a function to handle messages from the server in the given channel.
   *
   * @param channelId The channel identifier.
   * @param receiver  The handling function.
   */
  @ExpectPlatform
  public static void registerC2SReceiver(Identifier channelId, ServerPlayerEntity player, PacketReceiver receiver) {
    throw new AssertionError("Missing implementation of ServerNetworking.registerC2SReceiver()");
  }

  /**
   * Unregisters a channel.
   * <p>
   * Does nothing if already unregistered/not registered.
   *
   * @param channelId The channel identifier.
   */
  @ExpectPlatform
  public static void unregisterC2SReceiver(Identifier channelId, ServerPlayerEntity player) {
    throw new AssertionError("Missing implementation of ServerNetworking.unregisterC2SReceiver()");
  }

  /**
   * Register a function to call each time clients registers/unregisters the given channel id.
   *
   * @param channelId The channel identifier.
   * @param listener  The listener to call on registration changes.
   */
  @ExpectPlatform
  public static void addRegistrationChangeListener(Identifier channelId, RegistrationChangeListener listener) {
    throw new AssertionError("Missing implementation of ServerNetworking.addRegistrationChangeListener()");
  }

  /**
   * Client to server packet-handling function.
   */
  @FunctionalInterface
  public interface PacketReceiver {
    /**
     * Handles a client to server packet.
     *
     * @param sender The client that sent this packet.
     * @param buf    The packet data.
     */
    void handle(ServerPlayerEntity sender, PacketByteBuf buf);
  }


  /**
   * Client channel registration change listener.
   */
  @FunctionalInterface
  public interface RegistrationChangeListener {
    /**
     * Client channel registration change event.
     *
     * @param sender The player that triggered this event.
     * @param type   Whether the channel was registered on unregistered.
     */
    void onRegistrationChange(ServerPlayerEntity sender, RegistrationChangeType type);
  }
}
