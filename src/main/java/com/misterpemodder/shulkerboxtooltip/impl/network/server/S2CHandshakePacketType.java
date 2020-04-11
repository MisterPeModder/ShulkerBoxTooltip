package com.misterpemodder.shulkerboxtooltip.impl.network.server;

import com.misterpemodder.shulkerboxtooltip.impl.network.client.ClientConnectionHandler;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

public class S2CHandshakePacketType extends S2CPacketType<Integer> {
  public S2CHandshakePacketType(String id) {
    super(id);
  }

  @Override
  protected boolean readPacket(PacketContext context, PacketByteBuf buf) {
    ClientConnectionHandler.onHandshakeFinished(buf.readInt());
    return true;
  }

  @Override
  protected boolean writePacket(PacketByteBuf buf, Integer protocolVersion) {
    buf.writeInt(protocolVersion);
    return true;
  }
}
