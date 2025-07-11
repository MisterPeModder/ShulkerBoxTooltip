package com.misterpemodder.shulkerboxtooltip.impl.network.neoforge;

import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.C2SChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.C2SMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class NeoForgeC2SChannel<T> extends NeoForgeChannel<T> implements C2SChannel<T> {
  public NeoForgeC2SChannel(ResourceLocation id, MessageType<T> type) {
    super(id, type);
  }

  @Override
  public void registerFor(ServerPlayer player) {
    // NeoForge does not support dynamic channel registration
  }

  @Override
  public void unregisterFor(ServerPlayer player) {
    // NeoForge does not support dynamic channel registration
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void sendToServer(T message) {
    ClientPacketDistributor.sendToServer(new Payload<>(this.id, message));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public boolean canSendToServer() {
    ICommonPacketListener listener = Minecraft.getInstance().getConnection();
    return listener != null && listener.hasChannel(this.getId());
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void onDisconnect() {
  }

  @Override
  protected void onReceive(Payload<T> payload, IPayloadContext context) {
    if (context.flow().isServerbound()) {
      this.type.onReceive(payload.value(), new C2SMessageContext<>((ServerPlayer) context.player(), this));
    }
  }

  @Override
  protected void registerPayloadTypeInner(RegisterPayloadHandlersEvent event) {
    event.registrar("1").optional().commonToServer(this.id, this.codec, this::onReceive);
  }
}
