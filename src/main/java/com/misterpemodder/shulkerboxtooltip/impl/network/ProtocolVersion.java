package com.misterpemodder.shulkerboxtooltip.impl.network;

import net.minecraft.network.PacketByteBuf;

public class ProtocolVersion {
  public final int major;
  public final int minor;

  public static final ProtocolVersion CURRENT = new ProtocolVersion(1, 0);

  public ProtocolVersion(int major, int minor) {
    this.major = major;
    this.minor = minor;
  }

  public static ProtocolVersion readFromPacketBuf(PacketByteBuf buf) {
    int major = buf.readInt();
    int minor = buf.readInt();
    return new ProtocolVersion(major, minor);
  }

  public void writeToPacketBuf(PacketByteBuf buf) {
    buf.writeInt(this.major);
    buf.writeInt(this.minor);
  }
}
