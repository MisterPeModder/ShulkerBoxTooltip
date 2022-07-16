package com.misterpemodder.shulkerboxtooltip.mixin.fabric.client;

import com.misterpemodder.shulkerboxtooltip.fabric.ShulkerBoxTooltipClientFabric;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
  @Inject(at = @At("HEAD"),
    method = "Lnet/minecraft/client/Keyboard;onKey(JIIII)V")
  private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
    // update the pressed preview keys when key event was received on the game window
    if (window == MinecraftClient.getInstance().getWindow().getHandle())
      ShulkerBoxTooltipClientFabric.updatePreviewKeys();
  }
}
