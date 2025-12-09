package com.misterpemodder.shulkerboxtooltip.impl.network.fabric;

import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.Channel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

abstract class FabricChannel<T> implements Channel<T> {
  protected final CustomPacketPayload.Type<Payload<T>> id;
  protected final MessageType<T> type;
  protected final StreamCodec<RegistryFriendlyByteBuf, Payload<T>> codec;
  private boolean payloadTypeRegistered = false;


  protected FabricChannel(Identifier id, MessageType<T> type) {
    this.id = new CustomPacketPayload.Type<>(id);
    this.type = type;
    this.codec = StreamCodec.of(this::encodePayload, this::decodePayload);
  }

  @Override
  public Identifier getId() {
    return this.id.id();
  }

  @Override
  public MessageType<T> getMessageType() {
    return this.type;
  }

  @Override
  public void registerPayloadType() {
    if (this.payloadTypeRegistered) {
      return;
    }
    PayloadTypeRegistry.playC2S().register(this.id, this.codec);
    PayloadTypeRegistry.playS2C().register(this.id, this.codec);
    this.payloadTypeRegistered = true;
  }

  @Override
  public void onRegister(MessageContext<T> context) {
    this.type.onRegister(context);
  }

  @Override
  public void onUnregister(MessageContext<T> context) {
    this.type.onUnregister(context);
  }

  private void encodePayload(RegistryFriendlyByteBuf buf, Payload<T> message) {
    this.type.encode(message.value(), buf);
  }

  private Payload<T> decodePayload(RegistryFriendlyByteBuf buf) {
    return new Payload<>(this.id, this.type.decode(buf));
  }

}
