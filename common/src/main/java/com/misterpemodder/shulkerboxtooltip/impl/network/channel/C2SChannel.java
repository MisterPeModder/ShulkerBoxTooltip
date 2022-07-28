package com.misterpemodder.shulkerboxtooltip.impl.network.channel;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.C2SMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Client-to-server channel wrapper.
 *
 * @param <MSG> The message data type.
 */
public final class C2SChannel<MSG> extends Channel<MSG> {
  @Environment(EnvType.CLIENT)
  private boolean serverRegistered;

  /**
   * Creates a new client to server message channel.
   *
   * @param id          The channel id, must be unique.
   * @param messageType The channel description.
   */
  public C2SChannel(Identifier id, MessageType<MSG> messageType) {
    super(id, messageType);
    if (ShulkerBoxTooltip.isClient())
      this.serverRegistered = false;
  }

  /**
   * Registers handling of messages in this channel for the given player.
   *
   * @param player The player.
   */
  public void registerFor(ServerPlayerEntity player) {
    ServerNetworking.registerC2SReceiver(this.id, player, (sender, buf) -> {
      MSG message = this.messageType.decode(buf);
      MessageContext<MSG> context = new C2SMessageContext<>(sender, this);

      this.messageType.onReceive(message, context);
    });
  }

  /**
   * Unregisters handling of messages in this channel for the given player.
   *
   * @param player The player.
   */
  public void unregisterFor(ServerPlayerEntity player) {
    ServerNetworking.unregisterC2SReceiver(this.id, player);
  }

  /**
   * Sends a message to the server.
   *
   * @param message The message to send.
   */
  @Environment(EnvType.CLIENT)
  public void sendToServer(MSG message) {
    ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();

    if (handler == null) {
      ShulkerBoxTooltip.LOGGER.error("Cannot send message to the " + this.id + " channel while not in-game");
      return;
    }
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

    this.messageType.encode(message, buf);
    handler.sendPacket(ClientNetworking.createC2SPacket(this.id, buf));
  }

  /**
   * @return Whether a message can successfully be sent to the server.
   */
  @Environment(EnvType.CLIENT)
  public boolean canSendToServer() {
    return this.serverRegistered && MinecraftClient.getInstance().getNetworkHandler() != null;
  }

  @Override
  protected void onRegister(MessageContext<MSG> context) {
    if (context.getReceivingSide() == MessageContext.Side.CLIENT)
      this.serverRegistered = true;
    super.onRegister(context);
  }

  @Override
  protected void onUnregister(MessageContext<MSG> context) {
    if (context.getReceivingSide() == MessageContext.Side.CLIENT)
      this.serverRegistered = false;
    super.onUnregister(context);
  }

  @Environment(EnvType.CLIENT)
  public void onDisconnect() {
    this.serverRegistered = false;
  }
}
