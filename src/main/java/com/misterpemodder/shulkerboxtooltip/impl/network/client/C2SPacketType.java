package com.misterpemodder.shulkerboxtooltip.impl.network.client;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.PacketType;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;

public abstract class C2SPacketType<T> extends PacketType<T> {
  protected C2SPacketType(String id) {
    super(id);
  }

  @Override
  public void register() {
    if (ShulkerBoxTooltip.config.server.clientIntegration)
      ServerSidePacketRegistry.INSTANCE.register(this.id, this::readPacket);
  }

  public void sendToServer() {
    this.sendToServer(null);
  }

  public void sendToServer(T data) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

    if (!this.writePacket(buf, data))
      return;
    ClientSidePacketRegistry.INSTANCE.sendToServer(this.id, buf);
  }

  public boolean canServerReceive() {
    return ClientSidePacketRegistry.INSTANCE.canServerReceive(this.id);
  }
}
