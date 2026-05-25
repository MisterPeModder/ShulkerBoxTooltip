package com.misterpemodder.shulkerboxtooltip.impl.network.neoforge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.S2CChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.S2CMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class NeoForgeS2CChannel<T> extends NeoForgeChannel<T> implements S2CChannel<T> {
  public NeoForgeS2CChannel(Identifier id, MessageType<T> type) {
    super(id, type);
  }

  @Override
  public void register() {
    // NeoForge does not support dynamic channel registration
  }

  @Override
  public void unregister() {
    // NeoForge does not support dynamic channel registration
  }

  @Override
  public void sendTo(ServerPlayer player, T message) {
    PacketDistributor.sendToPlayer(player, new Payload<>(this.id, message));
  }

  @Override
  protected void onReceive(Payload<T> payload, IPayloadContext context) {
    if (context.flow().isClientbound() && ShulkerBoxTooltip.config.preview.serverIntegration)
      this.type.onReceive(payload.value(), new S2CMessageContext<>(this));
  }

  @Override
  protected void registerPayloadTypeInner(RegisterPayloadHandlersEvent event) {
    event.registrar("1").optional().commonToClient(this.id, this.codec, this::onReceive);
  }
}
