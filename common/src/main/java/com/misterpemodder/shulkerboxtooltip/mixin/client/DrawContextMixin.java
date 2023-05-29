package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.renderer.TooltipPositionAccess;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PositionAwareTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrawContext.class)
public class DrawContextMixin implements TooltipPositionAccess {
  private int shulkerboxtooltip$topY = 0;
  private int shulkerboxtooltip$bottomY = 0;

  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;"
      + "drawItems(Lnet/minecraft/client/font/TextRenderer;IILnet/minecraft/client/gui/DrawContext;)V"), method =
      "Lnet/minecraft/client/gui/DrawContext;drawTooltip("
          + "Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II"
          + "Lnet/minecraft/client/gui/tooltip/TooltipPositioner;)V")
  private void drawPosAwareComponent(TooltipComponent component, TextRenderer textRenderer, int x, int y,
      DrawContext context) {
    if (component instanceof PositionAwareTooltipComponent posAwareComponent) {
      //noinspection ConstantConditions
      posAwareComponent.drawItemsWithTooltipPosition(textRenderer, x, y, context, this.getTooltipTopYPosition(),
          this.getTooltipBottomYPosition());
    } else
      component.drawItems(textRenderer, x, y, context);
  }

  @Override
  public void setTooltipTopYPosition(int topY) {
    this.shulkerboxtooltip$topY = topY;
  }

  @Override
  public void setTooltipBottomYPosition(int bottomY) {
    this.shulkerboxtooltip$bottomY = bottomY;
  }

  @Override
  public int getTooltipTopYPosition() {
    return this.shulkerboxtooltip$topY;
  }

  @Override
  public int getTooltipBottomYPosition() {
    return this.shulkerboxtooltip$bottomY;
  }
}
