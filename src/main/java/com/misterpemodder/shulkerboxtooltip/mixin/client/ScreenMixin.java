package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PositionAwareTooltipComponent;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PositionAwareTooltipComponent.TooltipPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

@Mixin(Screen.class)
public class ScreenMixin {
  private int shulkerboxtooltip$topY = 0;
  private int shulkerboxtooltip$bottomY = 0;

  @ModifyArg(
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient"
          + "(Lnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/BufferBuilder;IIIIIII)V",
          ordinal = 2),
      method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents"
          + "(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V",
      index = 2)
  private int updateTooltipLeftAndBottomPos(Matrix4f matrix, BufferBuilder builder, int x1, int y1,
      int x2, int y2, int zOffset, int color1, int color2) {
    shulkerboxtooltip$topY = y1;
    shulkerboxtooltip$bottomY = y2;
    return x1;
  }

  @Redirect(at = @At(value = "INVOKE",
      target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawItems"
          + "(Lnet/minecraft/client/font/TextRenderer;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/item/ItemRenderer;I)V"),
      method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents"
          + "(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V")
  private void drawPosAwareComponent(TooltipComponent component, TextRenderer textRenderer, int x,
      int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
    if (component instanceof PositionAwareTooltipComponent posAwareComponent)
      posAwareComponent.drawItems(textRenderer, x, y, matrices, itemRenderer, z,
          new TooltipPosition((Screen) (Object) this, this.shulkerboxtooltip$topY, this.shulkerboxtooltip$bottomY));
    else
      component.drawItems(textRenderer, x, y, matrices, itemRenderer, z);
  }
}
