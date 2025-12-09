package com.misterpemodder.shulkerboxtooltip.impl.network.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.C2SChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.C2SMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import com.misterpemodder.shulkerboxtooltip.impl.util.EnvironmentUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

class FabricC2SChannel<T> extends FabricChannel<T> implements C2SChannel<T> {
  @Environment(EnvType.CLIENT)
  private boolean serverRegistered;

  public FabricC2SChannel(Identifier id, MessageType<T> type) {
    super(id, type);
    if (EnvironmentUtil.isClient())
      this.serverRegistered = false;
  }

  @Override
  public void registerFor(ServerPlayer player) {
    ServerGamePacketListenerImpl handler = player.connection;

    if (handler == null) {
      ShulkerBoxTooltip.LOGGER.error("Cannot register packet receiver for " + this.getId() + ", player is not in game");
      return;
    }
    ServerPlayNetworking.registerReceiver(handler, this.id, this::onReceive);
  }

  @Override
  public void unregisterFor(ServerPlayer player) {
    ServerGamePacketListenerImpl handler = player.connection;

    if (handler != null) {
      ServerPlayNetworking.unregisterReceiver(handler, this.getId());
    }
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void sendToServer(T message) {
    ClientPlayNetworking.send(new Payload<>(this.id, message));
  }

  @Override
  @Environment(EnvType.CLIENT)
  public boolean canSendToServer() {
    return this.serverRegistered && Minecraft.getInstance().getConnection() != null;
  }

  @Override
  public void onRegister(MessageContext<T> context) {
    if (context.getReceivingSide() == MessageContext.Side.CLIENT)
      this.serverRegistered = true;
    super.onRegister(context);
  }

  @Override
  public void onUnregister(MessageContext<T> context) {
    if (context.getReceivingSide() == MessageContext.Side.CLIENT)
      this.serverRegistered = false;
    super.onUnregister(context);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void onDisconnect() {
    this.serverRegistered = false;
  }

  private void onReceive(Payload<T> payload, ServerPlayNetworking.Context context) {
    this.type.onReceive(payload.value(), new C2SMessageContext<>(context.player(), this));
  }

}
