package com.misterpemodder.shulkerboxtooltip.impl.network.forge;

import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.Channel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;

abstract class ForgeChannel<T> implements Channel<T> {
  protected final CustomPacketPayload.Type<Payload<T>> id;
  protected final MessageType<T> type;
  protected final StreamCodec<FriendlyByteBuf, Payload<T>> codec;
  protected net.minecraftforge.network.Channel<Payload<T>> innerChannel;
  private boolean payloadTypeRegistered = false;

  protected ForgeChannel(Identifier id, MessageType<T> type) {
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
  @SuppressWarnings("unchecked")
  public void registerPayloadType() {
    if (this.payloadTypeRegistered) {
      return;
    }
    var c = ChannelBuilder.named(this.getId()) //
        .optional() //
        .payloadChannel() //
        .any() //
        .bidirectional() //
        .add(this.id, this.codec, this::onReceive) //
        .build();
    this.innerChannel = (net.minecraftforge.network.Channel<Payload<T>>) (net.minecraftforge.network.Channel<?>) c;
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

  protected abstract void onReceive(Payload<T> payload, CustomPayloadEvent.Context context);
}
