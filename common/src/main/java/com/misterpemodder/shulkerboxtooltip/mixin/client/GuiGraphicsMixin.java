package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewClientTooltipComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin implements GuiGraphicsExtensions {
  @Unique
  private int tooltipTopY = 0;
  @Unique
  private int mouseX = 0;
  @Unique
  private int mouseY = 0;

  @Redirect(at = @At(value = "INVOKE", target =
      "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;"
      + "positionTooltip(IIIIII)Lorg/joml/Vector2ic;"), method =
    "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/ResourceLocation;)V", require = 0)
  private Vector2ic captureTooltipYPosition(ClientTooltipPositioner positioner, int guiWidth, int guiHeight, int x,
      int y, int totalWidth, int totalHeight) {
    Vector2ic result = positioner.positionTooltip(guiWidth, guiHeight, x, y, totalWidth, totalHeight);
    this.setTooltipTopYPosition(result.y());
    return result;
  }

  @Redirect(at = @At(value = "INVOKE", target =
      "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;"
      + "renderImage(Lnet/minecraft/client/gui/Font;IIIILnet/minecraft/client/gui/GuiGraphics;)V"), method =
    "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/ResourceLocation;)V")
  private void renderImageWithMouse(ClientTooltipComponent component, Font font, int x, int y, int totalWidth,
      int totalHeight, GuiGraphics graphics) {
    if (component instanceof PreviewClientTooltipComponent previewComponent) {
      previewComponent.renderImageExtended(font, x, y, totalWidth, totalHeight, graphics, this.getMouseX(),
          this.getMouseY(), this.getTooltipTopYPosition());
    } else {
      component.renderImage(font, x, y, totalWidth, totalHeight, graphics);
    }
  }

  @Intrinsic
  public void setTooltipTopYPosition(int topY) {
    this.tooltipTopY = topY;
  }

  @Override
  @Intrinsic
  public int getTooltipTopYPosition() {
    return this.tooltipTopY;
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
