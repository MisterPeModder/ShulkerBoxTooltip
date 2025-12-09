package com.misterpemodder.shulkerboxtooltip.impl.network.forge;

import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.S2CChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.S2CMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.PacketDistributor;

public class ForgeS2CChannel<T> extends ForgeChannel<T> implements S2CChannel<T> {
  public ForgeS2CChannel(Identifier id, MessageType<T> type) {
    super(id, type);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void register() {
    // NeoForge does not support dynamic channel registration
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void unregister() {
    // NeoForge does not support dynamic channel registration
  }

  @Override
  public void sendTo(ServerPlayer player, T message) {
    this.innerChannel.send(new Payload<>(this.id, message), PacketDistributor.PLAYER.with(player));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  protected void onReceive(Payload<T> payload, CustomPayloadEvent.Context context) {
    if (context.isClientSide()) {
      this.type.onReceive(payload.value(), new S2CMessageContext<>(this));
    }
    context.setPacketHandled(true);
  }
}
