package com.misterpemodder.shulkerboxtooltip.mixin.client.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Inject(at = @At("HEAD"), method = "getTooltipImage()Ljava/util/Optional;", cancellable = true)
  private void onGetTooltipData(CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
    PreviewContext context = PreviewContext.builder((ItemStack) (Object) this).withOwner(
        ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player).build();

    PreviewProvider provider = ShulkerBoxTooltipApi.getProviderIfPreviewAvailable(context);
    if (provider != null)
      cir.setReturnValue(Optional.of(new PreviewTooltipComponent(provider, context)));
  }

  @Inject(at = @At("RETURN"), method =
      "getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;"
      + "Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;")
  private void onGetTooltip(Item.TooltipContext context, Player player, TooltipFlag type,
      CallbackInfoReturnable<List<Component>> cir) {
    var tooltip = cir.getReturnValue();
    ShulkerBoxTooltipClient.modifyStackTooltip((ItemStack) (Object) this, tooltip::addAll);
  }
}
