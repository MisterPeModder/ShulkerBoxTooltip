package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class PacketType {
  protected final Identifier id;

  protected PacketType(String id) {
    this.id = new Identifier(ShulkerBoxTooltip.MOD_ID, id);
  }

  public abstract void register();

  protected abstract void readPacket(PacketContext context, PacketByteBuf buf);

  protected abstract void writePacket(PacketByteBuf buf);
}
