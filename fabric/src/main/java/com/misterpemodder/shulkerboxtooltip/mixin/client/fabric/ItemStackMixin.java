package com.misterpemodder.shulkerboxtooltip.mixin.client.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Inject(at = @At("HEAD"), method = "getTooltipData()Ljava/util/Optional;", cancellable = true)
  private void onGetTooltipData(CallbackInfoReturnable<Optional<TooltipData>> ci) {
    PreviewContext context = PreviewContext.of((ItemStack) (Object) this,
        ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player);

    if (ShulkerBoxTooltipApi.isPreviewAvailable(context))
      ci.setReturnValue(Optional.of(
          new PreviewTooltipData(ShulkerBoxTooltipApi.getPreviewProviderForStack(context.stack()), context)));
  }

  @Inject(at = @At("RETURN"), method = "Lnet/minecraft/item/ItemStack;getTooltip"
      + "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;")
  private void onGetTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> ci) {
    var tooltip = ci.getReturnValue();
    ShulkerBoxTooltipClient.modifyStackTooltip((ItemStack) (Object) this, tooltip::addAll);
  }
}
