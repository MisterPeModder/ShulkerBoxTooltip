package com.misterpemodder.shulkerboxtooltip.mixin.client;

import java.util.List;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Inject(at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT,
      method = "Lnet/minecraft/item/ItemStack;getTooltip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;")
  private void onGetTooltip(PlayerEntity player, TooltipContext context,
      CallbackInfoReturnable<List<Text>> ci, List<Text> tooltip) {
    ShulkerBoxTooltipClient.modifyStackTooltip((ItemStack) (Object) this, tooltip);
  }
}
