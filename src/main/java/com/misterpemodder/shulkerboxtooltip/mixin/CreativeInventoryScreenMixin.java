package com.misterpemodder.shulkerboxtooltip.mixin;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemStack;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin {
  @Inject(at = @At(value = "INVOKE",
      target = "net/minecraft/client/gui/screen/ingame/CreativeInventoryScreen.renderTooltip(Ljava/util/List;II)V"),
      method = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;renderTooltip"
          + "(Lnet/minecraft/item/ItemStack;II)V")
  private void onDrawMousehoverTooltip(ItemStack stack, int mouseX, int mouseY, CallbackInfo ci) {
    if (ShulkerBoxTooltip.hasShulkerBoxPreview(stack))
      ShulkerBoxTooltip.drawShulkerBoxPreview((Screen) (Object) this, stack, mouseX, mouseY);
  }
}
