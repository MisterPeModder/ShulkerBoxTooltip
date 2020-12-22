package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

// TODO: Use fabric-networking-v1
@SuppressWarnings("deprecation")
public abstract class PacketType<T> {
  protected final Identifier id;

  protected PacketType(String id) {
    this.id = new Identifier(ShulkerBoxTooltip.MOD_ID, id);
  }

  public abstract void register();

  /**
   * Reads the recieved packet data.
   * 
   * @param context The packet context.
   * @param buf The packet byte buffer.
   * @return true on success, false otherwise.
   */
  protected abstract boolean readPacket(PacketContext context, PacketByteBuf buf);

  /**
   * Writes a packet.
   * 
   * @param buf The packet byte buffer.
   * @param data The data to write.
   * @return true on success, false otherwise.
   */
  protected boolean writePacket(PacketByteBuf buf, T data) {
    return writePacket(buf);
  }

  /**
   * Writes a packet (with no data).
   * 
   * @param buf The packet byte buffer.
   * @return true on success, false otherwise.
   */
  protected boolean writePacket(PacketByteBuf buf) {
    return true;
  }
}
