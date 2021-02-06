package com.misterpemodder.shulkerboxtooltip.impl.network;

import blue.endless.jankson.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;

public class ProtocolVersion {
  public final int major;
  public final int minor;

  public static final ProtocolVersion CURRENT = new ProtocolVersion(2, 0);

  public ProtocolVersion(int major, int minor) {
    this.major = major;
    this.minor = minor;
  }

  @Nullable
  public static ProtocolVersion readFromPacketBuf(PacketByteBuf buf) {
    try {
      return new ProtocolVersion(buf.readInt(), buf.readInt());
    } catch (RuntimeException e) {
      return null;
    }
  }

  public void writeToPacketBuf(PacketByteBuf buf) {
    buf.writeInt(this.major);
    buf.writeInt(this.minor);
  }

  public String toString() {
    return this.major + "." + this.minor;
  }
}
