package com.misterpemodder.shulkerboxtooltip.mixin;

import com.misterpemodder.shulkerboxtooltip.impl.hook.EnderChestInventoryPrevTagAccessor;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.NbtList;

@Mixin(EnderChestInventory.class)
public class EnderChestInventoryMixin implements EnderChestInventoryPrevTagAccessor {
  private NbtList shulkerboxtooltip$prevTags;

  @Override
  public NbtList shulkerboxtooltip$getPrevTags() {
    return this.shulkerboxtooltip$prevTags;
  }

  @Override
  public void shulkerboxtooltip$setPrevTags(NbtList tags) {
    this.shulkerboxtooltip$prevTags = tags;
  }
}
