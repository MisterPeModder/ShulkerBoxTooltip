package com.misterpemodder.shulkerboxtooltip.mixin.client;

import java.util.List;
import java.util.Optional;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Inject(at = @At("HEAD"),
      method = "Lnet/minecraft/item/ItemStack;getTooltipData()Ljava/util/Optional;",
      cancellable = true)
  private void onGetTooltipData(CallbackInfoReturnable<Optional<TooltipData>> ci) {
    PreviewContext context =
        PreviewContext.of((ItemStack) (Object) this, ShulkerBoxTooltipClient.client.player);

    if (ShulkerBoxTooltipApi.isPreviewAvailable(context))
      ci.setReturnValue(Optional.of(new PreviewTooltipData(
          ShulkerBoxTooltipApi.getPreviewProviderForStack(context.getStack()), context)));
  }

  @Inject(at = @At("RETURN"), method = "Lnet/minecraft/item/ItemStack;getTooltip"
      + "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;")
  private void onGetTooltip(PlayerEntity player, TooltipContext context,
      CallbackInfoReturnable<List<Text>> ci) {
    ShulkerBoxTooltipClient.modifyStackTooltip((ItemStack) (Object) this, ci.getReturnValue());
  }

  /*
  @Redirect(at = @At(value = "INVOKE",
      target = "Lnet/minecraft/nbt/NbtCompound;contains" + "(Ljava/lang/String;I)Z", ordinal = 0),
      method = "Lnet/minecraft/item/ItemStack;getTooltip"
          + "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)"
          + "Ljava/util/List;",
      slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=display", ordinal = 0)))
  private boolean removeLore(NbtCompound tag, String key, int type, @Nullable PlayerEntity player,
      TooltipContext context) {
    Item item = ((ItemStack) (Object) this).getItem();
  
    if (ShulkerBoxTooltip.config.main.tooltipType != ShulkerBoxTooltipType.VANILLA
        && item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ShulkerBoxBlock)
      return false;
    return tag.contains(key, type);
  }*/
}
