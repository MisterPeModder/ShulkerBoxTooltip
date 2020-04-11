package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.network.client.ClientConnectionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;

@Mixin(value = ClientPlayNetworkHandler.class, priority = 2000)
public class ClientPlayNetworkHandlerMixin {

  @Inject(at = @At("RETURN"), method = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;"
      + "onGameJoin(Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;)V")
  public void onGameJoin(CallbackInfo info) {
    ClientConnectionHandler.onJoinServer();
  }
}
