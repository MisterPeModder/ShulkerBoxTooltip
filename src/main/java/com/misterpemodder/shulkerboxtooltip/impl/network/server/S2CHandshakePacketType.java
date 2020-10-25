package com.misterpemodder.shulkerboxtooltip.impl.network.server;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ProtocolVersion;
import com.misterpemodder.shulkerboxtooltip.impl.network.client.ClientConnectionHandler;

import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

public class S2CHandshakePacketType extends S2CPacketType<ProtocolVersion> {
  public S2CHandshakePacketType(String id) {
    super(id);
  }

  @Override
  protected boolean readPacket(PacketContext context, PacketByteBuf buf) {
    ClientConnectionHandler.onHandshakeFinished(ProtocolVersion.readFromPacketBuf(buf));
    ShulkerBoxTooltip.config.readFromPacketBuf(buf);
    return true;
  }

  @Override
  protected boolean writePacket(PacketByteBuf buf, ProtocolVersion serverProtocolVersion) {
    serverProtocolVersion.writeToPacketBuf(buf);
    ShulkerBoxTooltip.savedConfig.writeToPacketBuf(buf);
    return true;
  }
}
