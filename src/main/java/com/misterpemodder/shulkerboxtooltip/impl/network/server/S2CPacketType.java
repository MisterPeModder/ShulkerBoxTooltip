package com.misterpemodder.shulkerboxtooltip.impl.network.server;

import com.misterpemodder.shulkerboxtooltip.impl.network.PacketType;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public abstract class S2CPacketType<T> extends PacketType<T> {
  protected S2CPacketType(String id) {
    super(id);
  }

  @Override
  public void register() {
    ClientSidePacketRegistry.INSTANCE.register(this.id, this::readPacket);
  }

  public void sendToPlayer(PlayerEntity player) {
    this.sendToPlayer(player, null);
  }

  public void sendToPlayer(PlayerEntity player, T data) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

    if (!this.writePacket(buf, data))
      return;
    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, this.id, buf);
  }

  public boolean canPlayerReceive(PlayerEntity player) {
    return ServerSidePacketRegistry.INSTANCE.canPlayerReceive(player, this.id);
  }
}
