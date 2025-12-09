package com.misterpemodder.shulkerboxtooltip.impl.network.fabric;

import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.S2CChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.S2CMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

class FabricS2CChannel<T> extends FabricChannel<T> implements S2CChannel<T> {
  public FabricS2CChannel(Identifier id, MessageType<T> type) {
    super(id, type);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void register() {
    ClientPlayNetworking.registerReceiver(this.id, this::onReceive);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void unregister() {
    ClientPlayNetworking.unregisterReceiver(this.getId());
  }

  @Override
  public void sendTo(ServerPlayer player, T message) {
    ServerPlayNetworking.send(player, new Payload<>(this.id, message));
  }

  @Environment(EnvType.CLIENT)
  private void onReceive(Payload<T> payload, ClientPlayNetworking.Context context) {
    this.type.onReceive(payload.value(), new S2CMessageContext<>(this));
  }
}
