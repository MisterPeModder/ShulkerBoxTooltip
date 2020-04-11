package com.misterpemodder.shulkerboxtooltip.impl.network.client;

import com.misterpemodder.shulkerboxtooltip.impl.network.PacketType;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;

public abstract class C2SPacketType extends PacketType {
  protected C2SPacketType(String id) {
    super(id);
  }

  @Override
  public void register() {
    ServerSidePacketRegistry.INSTANCE.register(this.id, this::readPacket);
  }

  public void sendToServer() {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

    this.writePacket(buf);
    ClientSidePacketRegistry.INSTANCE.sendToServer(this.id, buf);
  }

  public boolean canServerReceive() {
    return ClientSidePacketRegistry.INSTANCE.canServerReceive(this.id);
  }
}
