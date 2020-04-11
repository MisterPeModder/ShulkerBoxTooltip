package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.network.client.ClientConnectionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(value = MinecraftClient.class, priority = 2000)
public class MinecraftClientMixin {
    @Inject(at = @At("RETURN"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    public void afterDisconnect(Screen screen, CallbackInfo info) {
        ClientConnectionHandler.onQuitServer();
    }
}

