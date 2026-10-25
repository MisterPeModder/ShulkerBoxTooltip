package com.misterpemodder.shulkerboxtooltip.mixin;

import com.misterpemodder.shulkerboxtooltip.impl.hook.EnderChestListenerHolder;
import com.misterpemodder.shulkerboxtooltip.impl.network.EnderChestListener;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEnderChestContainer.class)
public class PlayerEnderChestContainerHookMixin implements EnderChestListenerHolder {
  @Unique
  @Nullable
  private EnderChestListener shulkerBoxTooltip$listener;

  @Unique
  public @Nullable EnderChestListener shulkerBoxTooltip$getListener() {
    return this.shulkerBoxTooltip$listener;
  }

  @Unique
  public void shulkerBoxTooltip$setListener(@Nullable EnderChestListener shulkerBoxTooltip$listener) {
    this.shulkerBoxTooltip$listener = shulkerBoxTooltip$listener;
  }

  // setChanged() override is added by PlayerEnderChestContainerMixin
  @Inject(method = "setChanged", at = @At("HEAD"))
  private void onSetChanged(CallbackInfo ci) {
    if (this.shulkerBoxTooltip$listener != null)
      this.shulkerBoxTooltip$listener.containerChanged();
  }

}
