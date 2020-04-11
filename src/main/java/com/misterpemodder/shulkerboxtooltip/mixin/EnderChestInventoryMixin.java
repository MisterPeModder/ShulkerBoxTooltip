package com.misterpemodder.shulkerboxtooltip.mixin;

import com.misterpemodder.shulkerboxtooltip.impl.hook.EnderChestInventoryPrevTagAccessor;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.ListTag;

@Mixin(EnderChestInventory.class)
public class EnderChestInventoryMixin implements EnderChestInventoryPrevTagAccessor {
  private ListTag shulkerboxtooltip$prevTags;

  @Override
  public ListTag shulkerboxtooltip$getPrevTags() {
    return this.shulkerboxtooltip$prevTags;
  }

  @Override
  public void shulkerboxtooltip$setPrevTags(ListTag tags) {
    this.shulkerboxtooltip$prevTags = tags;
  }
}
