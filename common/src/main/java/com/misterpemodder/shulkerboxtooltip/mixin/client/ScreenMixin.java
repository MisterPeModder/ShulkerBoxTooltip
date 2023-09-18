package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.renderer.DrawContext;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.DrawContextAccess;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.DrawContextExtensions;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
@SuppressWarnings("SpellCheckingInspection")
public class ScreenMixin implements DrawContextAccess {
  @Unique
  private final DrawContext drawContext = new DrawContext((Screen) (Object) this);

  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipPositioner;getPosition(Lnet/minecraft/client/gui/screen/Screen;IIII)Lorg/joml/Vector2ic;", ordinal = 0), method =
      "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents"
          + "(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;"
          + "IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V")
  private Vector2ic updateTooltipLeftAndBottomPos(TooltipPositioner positioner, Screen screen, int startX, int startY,
      int width, int height) {
    Vector2ic tooltipPos = positioner.getPosition(screen, startX, startY, width, height);
    DrawContextExtensions posAccess = this.drawContext;
    posAccess.setTooltipTopYPosition(tooltipPos.y() - 3);
    posAccess.setTooltipBottomYPosition(posAccess.getTooltipTopYPosition() + height + 6);
    return tooltipPos;
  }

  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawItems"
      + "(Lnet/minecraft/client/font/TextRenderer;IILnet/minecraft/client/util/math/MatrixStack;"
      + "Lnet/minecraft/client/render/item/ItemRenderer;)V"), method =
      "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents"
          + "(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;"
          + "IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V")
  private void drawPosAwareComponent(TooltipComponent component, TextRenderer textRenderer, int x, int y,
      MatrixStack matrices, ItemRenderer itemRenderer) {
    this.drawContext.update(matrices, itemRenderer);
    this.drawContext.drawItems(component, textRenderer, x, y);
  }

  @Override
  public DrawContext getDrawContext() {
    return this.drawContext;
  }
}
