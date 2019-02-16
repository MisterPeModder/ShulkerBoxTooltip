package com.misterpemodder.shulkerboxtooltip.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;

@Mixin(ShulkerBoxBlockEntity.class)
public interface ShulkerBoxSlotsAccessor {
  @Accessor("AVAILABLE_SLOTS")
  public static int[] getAvailableSlots() {
    return null;
  }
}
