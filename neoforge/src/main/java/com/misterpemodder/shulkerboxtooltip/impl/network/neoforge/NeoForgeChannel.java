package com.misterpemodder.shulkerboxtooltip.impl.network.neoforge;

import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.Channel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

abstract class NeoForgeChannel<T> implements Channel<T> {
  protected final CustomPacketPayload.Type<Payload<T>> id;
  protected final MessageType<T> type;
  protected final StreamCodec<FriendlyByteBuf, Payload<T>> codec;
  private boolean payloadTypeRegistered = false;


  protected NeoForgeChannel(ResourceLocation id, MessageType<T> type) {
    this.id = new CustomPacketPayload.Type<>(id);
    this.type = type;
    this.codec = StreamCodec.of(this::encodePayload, this::decodePayload);
  }

  @Override
  public ResourceLocation getId() {
    return this.id.id();
  }

  @Override
  public MessageType<T> getMessageType() {
    return this.type;
  }

  @Override
  public void registerPayloadType() {
    // payload registration is handled by the RegisterPayloadHandlersEvent
  }

  public void registerPayloadTypeDeferred(RegisterPayloadHandlersEvent event) {
    if (this.payloadTypeRegistered) {
      return;
    }
    this.registerPayloadTypeInner(event);
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

  private void encodePayload(FriendlyByteBuf buf, Payload<T> message) {
    this.type.encode(message.value(), buf);
  }

  private Payload<T> decodePayload(FriendlyByteBuf buf) {
    return new Payload<>(this.id, this.type.decode(buf));
  }

  protected abstract void onReceive(Payload<T> payload, IPayloadContext context);

  protected abstract void registerPayloadTypeInner(RegisterPayloadHandlersEvent event);
}
