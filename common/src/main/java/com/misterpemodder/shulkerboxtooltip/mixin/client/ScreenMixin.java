package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.renderer.DrawContext;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.DrawContextAccess;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.DrawContextExtensions;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
@SuppressWarnings("SpellCheckingInspection")
public class ScreenMixin implements DrawContextAccess {
  @Unique
  private final DrawContext drawContext = new DrawContext((Screen) (Object) this);

  @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient"
      + "(Lnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/BufferBuilder;IIIIIII)V", ordinal = 2), method =
      "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents"
          + "(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V", index = 2)
  private int updateTooltipLeftAndBottomPos(Matrix4f matrix, BufferBuilder builder, int x1, int y1, int x2, int y2,
      int zOffset, int color1, int color2) {
    DrawContextExtensions posAccess = this.drawContext;
    posAccess.setTooltipTopYPosition(y1);
    posAccess.setTooltipBottomYPosition(y2);
    return x1;
  }

  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawItems(Lnet/minecraft/client/font/TextRenderer;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/item/ItemRenderer;I)V"), method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V")
  private void drawPosAwareComponent(TooltipComponent component, TextRenderer textRenderer, int x, int y,
      MatrixStack matrices, ItemRenderer itemRenderer, int z) {
    this.drawContext.update(matrices, itemRenderer);
    this.drawContext.setZ(z);
    this.drawContext.drawItems(component, textRenderer, x, y, z);
  }

  @Override
  public DrawContext getDrawContext() {
    return this.drawContext;
  }
}
