package com.misterpemodder.shulkerboxtooltip.mixin;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.hook.ShulkerPreviewPosGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;

@Mixin(Screen.class)
public final class ScreenMixin implements ShulkerPreviewPosGetter {
  private int shulkerboxtooltip$startX = 0;
  private int shulkerboxtooltip$topY = 0;
  private int shulkerboxtooltip$bottomY = 0;

  @Inject(at = @At("RETURN"), method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip"
      + "(Lnet/minecraft/item/ItemStack;II)V")
  private void onDrawMousehoverTooltip(ItemStack stack, int mouseX, int mouseY, CallbackInfo ci) {
    if (ShulkerBoxTooltip.hasShulkerBoxPreview(stack))
      ShulkerBoxTooltip.drawShulkerBoxPreview((Screen) (Object) this, stack, mouseX, mouseY);
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
          target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient(IIIIII)V", ordinal = 2),
      method = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Ljava/util/List;II)V",
      index = 0)
  private int updateTooltipLeftAndBottomPos(int x1, int y1, int x2, int y2, int color1,
      int color2) {
    shulkerboxtooltip$topY = y1;
    shulkerboxtooltip$bottomY = y2;
    return (shulkerboxtooltip$startX = x1);
  }
}
