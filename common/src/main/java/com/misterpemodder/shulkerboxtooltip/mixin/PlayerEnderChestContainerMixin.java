package com.misterpemodder.shulkerboxtooltip.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEnderChestContainer.class)
@Implements(@Interface(iface = Container.class, prefix = "container$"))
public abstract class PlayerEnderChestContainerMixin extends SimpleContainer {
  @Intrinsic
  public void container$setChanged() {
    // Adds setChanged() (if not already present) for PlayerEnderChestContainerHookMixin to hook into
    super.setChanged();
  }
}
