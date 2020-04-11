package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 2000)
public class ClientPlayNetworkHandlerMixin {

  @Inject(at = @At("RETURN"), method = "onGameJoin")
  public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
    ShulkerBoxTooltipClient.initPreviewItemsMap();
  }
}
