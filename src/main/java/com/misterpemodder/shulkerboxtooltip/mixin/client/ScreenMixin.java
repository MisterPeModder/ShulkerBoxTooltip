package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.impl.hook.ShulkerPreviewPosGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Mixin(Screen.class)
public final class ScreenMixin implements ShulkerPreviewPosGetter {
  private int shulkerboxtooltip$startX = 0;
  private int shulkerboxtooltip$topY = 0;
  private int shulkerboxtooltip$bottomY = 0;

  @Inject(at = @At("RETURN"), method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip"
      + "(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V")
  private void onDrawMousehoverTooltip(MatrixStack matrix, ItemStack stack, int mouseX, int mouseY,
      CallbackInfo ci) {
    ShulkerBoxTooltipClient.drawIfPreviewAvailable((Screen) (Object) this, stack);
  }

  @Override
  public int shulkerboxtooltip$getStartX() {
    return this.shulkerboxtooltip$startX;
  }

  @Override
  public int shulkerboxtooltip$getTopY() {
    return this.shulkerboxtooltip$topY;
  }

  @Override
  public int shulkerboxtooltip$getBottomY() {
    return this.shulkerboxtooltip$bottomY;
  }

  @ModifyArg(
      at = @At(value = "INVOKE",
          target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient"
              + "(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
          ordinal = 2),
      method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip"
          + "(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V",
      index = 1)
  private int updateTooltipLeftAndBottomPos(MatrixStack matrix, int x1, int y1, int x2, int y2,
      int color1, int color2) {
    shulkerboxtooltip$topY = y1;
    shulkerboxtooltip$bottomY = y2;
    return (shulkerboxtooltip$startX = x1);
  }
}
