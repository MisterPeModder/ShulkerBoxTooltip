package com.misterpemodder.shulkerboxtooltip.impl.network.server;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

public class S2CServerAvailablePacketType extends S2CPacketType {
  public S2CServerAvailablePacketType(String id) {
    super(id);
  }

  @Override
  protected void readPacket(PacketContext context, PacketByteBuf buf) {
    ShulkerBoxTooltipClient.serverProtocolVersion = buf.readInt();
    ShulkerBoxTooltipClient.serverAvailable = true;
  }

  @Override
  protected void writePacket(PacketByteBuf buf) {
    buf.writeInt(ShulkerBoxTooltip.PROTOCOL_VERSION);
  }
}
