package com.misterpemodder.shulkerboxtooltip.impl.network.channel;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.RegistrationChangeType;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.C2SMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.S2CMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.minecraft.util.Identifier;

/**
 * Base network channel wrapper.
 *
 * @param <MSG> The message data type.
 */
public sealed class Channel<MSG> permits C2SChannel, S2CChannel {
  protected final Identifier id;
  protected final MessageType<MSG> messageType;

  protected Channel(Identifier id, MessageType<MSG> messageType) {
    this.id = id;
    this.messageType = messageType;

    ServerNetworking.addRegistrationChangeListener(this.id,
        (sender, type) -> this.onRegistrationChange(type, new C2SMessageContext<>(sender, this)));

    if (ShulkerBoxTooltip.isClient()) {
      ClientNetworking.addRegistrationChangeListener(this.id,
          type -> this.onRegistrationChange(type, new S2CMessageContext<>(this)));
    }
  }

  public Identifier getId() {
    return this.id;
  }

  private void onRegistrationChange(RegistrationChangeType type, MessageContext<MSG> context) {
    switch (type) {
      case REGISTER -> this.onRegister(context);
      case UNREGISTER -> this.onUnregister(context);
    }
  }

  protected void onRegister(MessageContext<MSG> context) {
    this.messageType.onRegister(context);
  }

  protected void onUnregister(MessageContext<MSG> context) {
    this.messageType.onUnregister(context);
  }
}
