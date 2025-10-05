package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

  /**
   * Makes the current mouse position available via extensions to the GuiGraphics instance.
   */
  @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"), method = "renderWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphics;IIF)V")
  private void captureMousePosition(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    GuiGraphicsExtensions extensions = (GuiGraphicsExtensions) graphics;
    extensions.setMouseY(mouseY);
    extensions.setMouseX(mouseX);
  }

}
