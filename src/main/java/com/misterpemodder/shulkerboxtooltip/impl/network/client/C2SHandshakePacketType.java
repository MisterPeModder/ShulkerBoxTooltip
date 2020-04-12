package com.misterpemodder.shulkerboxtooltip.impl.network.client;

import com.misterpemodder.shulkerboxtooltip.impl.network.ProtocolVersion;
import com.misterpemodder.shulkerboxtooltip.impl.network.server.ServerConnectionHandler;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class C2SHandshakePacketType extends C2SPacketType<ProtocolVersion> {
  public C2SHandshakePacketType(String id) {
    super(id);
  }

  @Override
  protected boolean readPacket(PacketContext context, PacketByteBuf buf) {
    ServerConnectionHandler.onHandshakeAttempt((ServerPlayerEntity) context.getPlayer(),
        ProtocolVersion.readFromPacketBuf(buf));
    return true;
  }

  @Override
  protected boolean writePacket(PacketByteBuf buf, ProtocolVersion clientProtocolVersion) {
    clientProtocolVersion.writeToPacketBuf(buf);
    return true;
  }
}
