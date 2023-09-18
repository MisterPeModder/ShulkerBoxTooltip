package com.misterpemodder.shulkerboxtooltip.impl.network.channel;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.S2CMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Server-to-client channel wrapper.
 *
 * @param <MSG> The message data type.
 */
public final class S2CChannel<MSG> extends Channel<MSG> {
  /**
   * Creates a new server to client message channel.
   *
   * @param id          The channel id, must be unique.
   * @param messageType The channel description.
   */
  public S2CChannel(Identifier id, MessageType<MSG> messageType) {
    super(id, messageType);
  }

  /**
   * Registers handling of messages in this channel from the server.
   */
  @Environment(EnvType.CLIENT)
  public void register() {
    ClientNetworking.registerS2CReceiver(this.id, buf -> {
      MSG message = this.messageType.decode(buf);
      MessageContext<MSG> context = new S2CMessageContext<>(this);

      this.messageType.onReceive(message, context);
    });
  }

  /**
   * Unregisters handling of messages in this channel from the server.
   */
  @Environment(EnvType.CLIENT)
  public void unregister() {
    ClientNetworking.unregisterS2CReceiver(this.id);
  }

  /**
   * Sends a message to a specific player.
   *
   * @param player  The target player.
   * @param message The message to send.
   */
  public void sendTo(ServerPlayerEntity player, MSG message) {
    ServerPlayNetworkHandler handler = player.networkHandler;

    if (handler == null) {
      ShulkerBoxTooltip.LOGGER.error(
          "Cannot send message to the " + this.id + " channel while player " + player.getName() + " is not in-game");
      return;
    }
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

    this.messageType.encode(message, buf);
    handler.sendPacket(ServerNetworking.createS2CPacket(this.id, buf));
  }
}
