package com.misterpemodder.shulkerboxtooltip.impl.network.message;

import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Updates a client's ender chest contents.
 *
 * @param nbtInventory NBT-serialized ender chest inventory.
 */
public record S2CEnderChestUpdate(@Nullable ListTag nbtInventory) {
  private static final S2CEnderChestUpdate EMPTY = new S2CEnderChestUpdate(null);

  public static S2CEnderChestUpdate create(PlayerEnderChestContainer inventory, HolderLookup.Provider registries) {
    var valueOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
    inventory.storeAsSlots(valueOutput.list("inv", ItemStackWithSlot.CODEC));
    return new S2CEnderChestUpdate(valueOutput.buildResult().getListOrEmpty("inv"));
  }

  public static class Type implements MessageType<S2CEnderChestUpdate> {
    @Override
    public void encode(S2CEnderChestUpdate message, FriendlyByteBuf buf) {
      CompoundTag compound = new CompoundTag();

      compound.put("inv", Objects.requireNonNull(message.nbtInventory));
      buf.writeNbt(compound);
    }

    @Override
    public S2CEnderChestUpdate decode(FriendlyByteBuf buf) {
      return Optional.ofNullable(buf.readNbt()) //
          .flatMap(compound -> compound.getList("inv")) //
          .map(S2CEnderChestUpdate::new) //
          .orElse(EMPTY);
    }

    @Override
    public void onReceive(S2CEnderChestUpdate message, MessageContext<S2CEnderChestUpdate> context) {
      if (message.nbtInventory == null)
        return;

      Minecraft.getInstance().execute(() -> {
        if (Minecraft.getInstance().player != null) {
          var player = Minecraft.getInstance().player;

          var wrappedList = new CompoundTag();
          wrappedList.put("inv", message.nbtInventory);

          var valueInput = TagValueInput.create(ProblemReporter.DISCARDING, player.registryAccess(), wrappedList);
          player.getEnderChestInventory().fromSlots(valueInput.listOrEmpty("inv", ItemStackWithSlot.CODEC));
        }
      });
    }
  }
}
