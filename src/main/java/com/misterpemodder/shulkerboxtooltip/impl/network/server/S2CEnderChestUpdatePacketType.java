package com.misterpemodder.shulkerboxtooltip.impl.network.server;

import com.misterpemodder.shulkerboxtooltip.impl.hook.EnderChestInventoryPrevTagAccessor;
import com.misterpemodder.shulkerboxtooltip.impl.network.client.ClientConnectionHandler;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.PacketByteBuf;

public class S2CEnderChestUpdatePacketType extends S2CPacketType<EnderChestInventory> {
  public S2CEnderChestUpdatePacketType(String id) {
    super(id);
  }

  @Override
  protected boolean readPacket(PacketContext context, PacketByteBuf buf) {
    CompoundTag compound = buf.readCompoundTag();

    if (!compound.contains("inv", NbtType.LIST))
      return false;
    ListTag tags = compound.getList("inv", NbtType.COMPOUND);

    context.getTaskQueue().execute(() -> ClientConnectionHandler.runWhenConnected(() -> {
      MinecraftClient client = MinecraftClient.getInstance();
      client.player.getEnderChestInventory().readTags(tags);
    }));
    return true;
  }

  @Override
  protected boolean writePacket(PacketByteBuf buf, EnderChestInventory inventory) {
    CompoundTag compound = new CompoundTag();
    ListTag previous =
        ((EnderChestInventoryPrevTagAccessor) inventory).shulkerboxtooltip$getPrevTags();
    ListTag current = inventory.getTags();

    // Check if the inventory has been modified
    if (current.equals(previous))
      return false;
    ((EnderChestInventoryPrevTagAccessor) inventory).shulkerboxtooltip$setPrevTags(current);

    compound.put("inv", current);
    buf.writeCompoundTag(compound);
    return true;
  }
}
