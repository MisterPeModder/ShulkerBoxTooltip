package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PositionAwareTooltipComponent;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PositionAwareTooltipComponent.TooltipPosition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer.RectangleRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
@SuppressWarnings("SpellCheckingInspection")
public class ScreenMixin {
  private int shulkerboxtooltip$topY = 0;
  private int shulkerboxtooltip$bottomY = 0;

  @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;render"
      + "(Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer$RectangleRenderer;"
      + "Lorg/joml/Matrix4f;Lnet/minecraft/client/render/BufferBuilder;IIIII)V", ordinal = 0), method =
      "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents"
          + "(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;"
          + "IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", index = 0)
  private RectangleRenderer updateTooltipLeftAndBottomPos(RectangleRenderer renderer, Matrix4f matrix,
      BufferBuilder buffer, int x, int y, int width, int height, int z) {
    this.shulkerboxtooltip$topY = y - 3;
    this.shulkerboxtooltip$bottomY = this.shulkerboxtooltip$topY + height + 6;
    return renderer;
  }

  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawItems"
      + "(Lnet/minecraft/client/font/TextRenderer;IILnet/minecraft/client/util/math/MatrixStack;"
      + "Lnet/minecraft/client/render/item/ItemRenderer;)V"), method =
      "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents"
          + "(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;"
          + "IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V")
  private void drawPosAwareComponent(TooltipComponent component, TextRenderer textRenderer, int x, int y,
      MatrixStack matrices, ItemRenderer itemRenderer) {
    if (component instanceof PositionAwareTooltipComponent posAwareComponent) {
      //noinspection ConstantConditions
      posAwareComponent.drawItems(textRenderer, x, y, matrices, itemRenderer,
          new TooltipPosition((Screen) (Object) this, this.shulkerboxtooltip$topY, this.shulkerboxtooltip$bottomY));
    } else
      component.drawItems(textRenderer, x, y, matrices, itemRenderer);
  }
}
