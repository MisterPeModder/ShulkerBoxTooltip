package com.misterpemodder.shulkerboxtooltip.mixin;

import com.misterpemodder.shulkerboxtooltip.impl.network.server.ServerConnectionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
  @Inject(at = @At("RETURN"),
      method = "Lnet/minecraft/server/PlayerManager;"
          + "onPlayerConnect(Lnet/minecraft/network/ClientConnection;"
          + "Lnet/minecraft/server/network/ServerPlayerEntity;)V")
  private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player,
      CallbackInfo ci) {
    ServerConnectionHandler.onPlayerConnect(player);
  }
}
