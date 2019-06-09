package com.misterpemodder.shulkerboxtooltip.mixin;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
  @Inject(at = @At("HEAD"),
      method = "Lnet/minecraft/client/MinecraftClient;joinWorld(Lnet/minecraft/client/world/ClientWorld;)V")
  private void onJoinWorld(CallbackInfo ci) {
    ShulkerBoxTooltip.initPreviewItemsMap();
  }
}
