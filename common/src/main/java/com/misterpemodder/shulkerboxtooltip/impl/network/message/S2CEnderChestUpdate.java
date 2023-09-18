package com.misterpemodder.shulkerboxtooltip.impl.network.message;

import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Updates a client's ender chest contents.
 *
 * @param nbtInventory NBT-serialized ender chest inventory.
 */
public record S2CEnderChestUpdate(@Nullable NbtList nbtInventory) {
  public static S2CEnderChestUpdate create(EnderChestInventory inventory) {
    return new S2CEnderChestUpdate(inventory.toNbtList());
  }

  public static class Type implements MessageType<S2CEnderChestUpdate> {
    @Override
    public void encode(S2CEnderChestUpdate message, PacketByteBuf buf) {
      NbtCompound compound = new NbtCompound();

      compound.put("inv", Objects.requireNonNull(message.nbtInventory));
      buf.writeNbt(compound);
    }

    @Override
    public S2CEnderChestUpdate decode(PacketByteBuf buf) {
      NbtCompound compound = buf.readNbt();

      if (compound == null || !compound.contains("inv", NbtType.LIST))
        return new S2CEnderChestUpdate(null);
      return new S2CEnderChestUpdate(compound.getList("inv", NbtType.COMPOUND));
    }

    @Override
    public void onReceive(S2CEnderChestUpdate message, MessageContext<S2CEnderChestUpdate> context) {
      if (message.nbtInventory == null)
        return;

      MinecraftClient.getInstance().execute(() -> {
        if (MinecraftClient.getInstance().player != null)
          MinecraftClient.getInstance().player.getEnderChestInventory().readNbtList(message.nbtInventory);
      });
    }
  }
}
