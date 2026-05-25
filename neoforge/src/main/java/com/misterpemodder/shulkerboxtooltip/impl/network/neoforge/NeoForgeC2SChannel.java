package com.misterpemodder.shulkerboxtooltip.impl.network.neoforge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.C2SChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.C2SMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class NeoForgeC2SChannel<T> extends NeoForgeChannel<T> implements C2SChannel<T> {
  public NeoForgeC2SChannel(Identifier id, MessageType<T> type) {
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
  public void sendToServer(T message) {
    ClientPacketDistributor.sendToServer(new Payload<>(this.id, message));
  }

  @Override
  public boolean canSendToServer() {
    ICommonPacketListener listener = Minecraft.getInstance().getConnection();
    return listener != null && listener.hasChannel(this.getId());
  }

  @Override
  public void onDisconnect() {
  }

  @Override
  protected void onReceive(Payload<T> payload, IPayloadContext context) {
    if (context.flow().isServerbound() && ShulkerBoxTooltip.config.server.clientIntegration)
      this.type.onReceive(payload.value(), new C2SMessageContext<>((ServerPlayer) context.player(), this));
  }

  @Override
  protected void registerPayloadTypeInner(RegisterPayloadHandlersEvent event) {
    event.registrar("1").optional().commonToServer(this.id, this.codec, this::onReceive);
  }
}
