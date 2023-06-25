package com.misterpemodder.shulkerboxtooltip.mixin.client.forge;

import com.misterpemodder.shulkerboxtooltip.impl.hook.DrawContextExtensions;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TooltipBackgroundRenderer.class)
public class TooltipBackgroundRendererMixin {
  @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;"
      + "renderTooltipBackground(Lnet/minecraft/client/gui/DrawContext;IIIIIIIII)V")
  private static void updateTooltipLeftAndBottomPosInForgeHook(DrawContext context, int x, int y, int width, int height,
      int z, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom, CallbackInfo ci) {
    DrawContextExtensions posAccess = (DrawContextExtensions) context;
    posAccess.setTooltipTopYPosition(y - 3);
    posAccess.setTooltipBottomYPosition(posAccess.getTooltipTopYPosition() + height + 6);
  }
}
