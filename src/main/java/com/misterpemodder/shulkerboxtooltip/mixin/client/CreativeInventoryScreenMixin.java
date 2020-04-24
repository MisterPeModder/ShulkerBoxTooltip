package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin {
  @Inject(
      at = @At(value = "INVOKE",
          target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;renderTooltip"
              + "(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V",
          shift = Shift.AFTER),
      method = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;renderTooltip"
          + "(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V")
  private void onDrawMousehoverTooltip(MatrixStack matrix, ItemStack stack, int mouseX, int mouseY,
      CallbackInfo ci) {
    ShulkerBoxTooltipClient.drawIfPreviewAvailable((Screen) (Object) this, stack);
  }
}
