package com.misterpemodder.shulkerboxtooltip.mixin;

import com.misterpemodder.shulkerboxtooltip.impl.network.server.S2CPacketTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(value = PlayerManager.class, priority = 2000)
public class PlayerManagerMixin {
  @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE",
      target = "Lnet/minecraft/network/packet/s2c/play/DifficultyS2CPacket;<init>(Lnet/minecraft/world/Difficulty;Z)V"))
  public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player,
      CallbackInfo info) {
    if (S2CPacketTypes.SERVER_AVAILABLE.canPlayerReceive(player))
      S2CPacketTypes.SERVER_AVAILABLE.sendToPlayer(player);
  }
}
