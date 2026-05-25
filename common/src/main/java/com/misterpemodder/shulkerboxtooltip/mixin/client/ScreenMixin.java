package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.hook.EnderChestScreenDetector;
import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

  @Inject(at = @At("HEAD"), method = "onClose()V")
  private void onScreenClosed(CallbackInfo ci) {
    EnderChestScreenDetector.INSTANCE.onScreenClose((Screen) (Object) this);
  }

  /**
   * Makes the current mouse position available via extensions to the GuiGraphicsExtractor instance.
   */
  @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"), method = "extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V")
  private void captureMousePosition(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    GuiGraphicsExtensions extensions = (GuiGraphicsExtensions) graphics;
    extensions.setMouseY(mouseY);
    extensions.setMouseX(mouseX);
  }

}
