package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.renderer.DrawContextExtensions;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PositionAwareTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrawContext.class)
public class DrawContextMixin implements DrawContextExtensions {
  @Unique
  private int tooltipTopYPosition = 0;
  @Unique
  private int tooltipBottomYPosition = 0;
  @Unique
  private int mouseX = 0;
  @Unique
  private int mouseY = 0;

  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;"
      + "drawItems(Lnet/minecraft/client/font/TextRenderer;IILnet/minecraft/client/gui/DrawContext;)V"), method =
      "Lnet/minecraft/client/gui/DrawContext;drawTooltip("
          + "Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II"
          + "Lnet/minecraft/client/gui/tooltip/TooltipPositioner;)V")
  private void drawPosAwareComponent(TooltipComponent component, TextRenderer textRenderer, int x, int y,
      DrawContext context) {
    if (component instanceof PositionAwareTooltipComponent posAwareComponent) {
      posAwareComponent.drawItemsWithTooltipPosition(textRenderer, x, y, context, this.getTooltipTopYPosition(),
          this.getTooltipBottomYPosition(), this.getMouseX(), this.getMouseY());
    } else {
      component.drawItems(textRenderer, x, y, context);
    }
  }

  @Override
  @Intrinsic
  public void setTooltipTopYPosition(int topY) {
    this.tooltipTopYPosition = topY;
  }

  @Override
  @Intrinsic
  public void setTooltipBottomYPosition(int bottomY) {
    this.tooltipBottomYPosition = bottomY;
  }

  @Override
  @Intrinsic
  public int getTooltipTopYPosition() {
    return this.tooltipTopYPosition;
  }

  @Override
  @Intrinsic
  public int getTooltipBottomYPosition() {
    return this.tooltipBottomYPosition;
  }

  @Override
  @Intrinsic
  public void setMouseX(int mouseX) {
    this.mouseX = mouseX;
  }

  @Override
  @Intrinsic
  public int getMouseX() {
    return this.mouseX;
  }

  @Override
  @Intrinsic
  public void setMouseY(int mouseY) {
    this.mouseY = mouseY;
  }

  @Override
  @Intrinsic
  public int getMouseY() {
    return this.mouseY;
  }

}
