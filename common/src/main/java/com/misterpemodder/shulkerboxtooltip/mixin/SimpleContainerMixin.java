package com.misterpemodder.shulkerboxtooltip.mixin;

import com.misterpemodder.shulkerboxtooltip.impl.network.EnderChestInventoryListener;
import net.minecraft.world.SimpleContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleContainer.class)
public class SimpleContainerMixin {
  @Inject(method = "setChanged", at = @At("TAIL"))
  private void onSetChanged(CallbackInfo ci) {
    EnderChestInventoryListener.onContainerChanged((SimpleContainer) (Object) this);
  }
}
