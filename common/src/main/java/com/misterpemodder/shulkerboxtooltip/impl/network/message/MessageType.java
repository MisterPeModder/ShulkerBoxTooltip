package com.misterpemodder.shulkerboxtooltip.impl.network.message;

import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.C2SMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.S2CMessageContext;
import net.minecraft.network.PacketByteBuf;

/**
 * Describes a message.
 *
 * @param <MSG> The message data.
 */
public interface MessageType<MSG> {
  /**
   * Writes the message to the packet byte buffer.
   *
   * @param message The message to encode.
   * @param buf     The buffer.
   */
  void encode(MSG message, PacketByteBuf buf);

  /**
   * Reads a message from the given buffer.
   *
   * @param buf The buffer.
   * @return The decoded message.
   */
  MSG decode(PacketByteBuf buf);

  /**
   * Handles the given message.
   *
   * @param message The message to handle.
   * @param context Either an instance of {@link C2SMessageContext} or {@link S2CMessageContext}
   */
  void onReceive(MSG message, MessageContext<MSG> context);

  /**
   * Called when the message is registered.
   *
   * @param context Either an instance of {@link C2SMessageContext} or {@link S2CMessageContext}
   */
  default void onRegister(MessageContext<MSG> context) {
  }

  /**
   * Called when the message is unregistered.
   *
   * @param context Either an instance of {@link C2SMessageContext} or {@link S2CMessageContext}
   */
  default void onUnregister(MessageContext<MSG> context) {
  }
}
