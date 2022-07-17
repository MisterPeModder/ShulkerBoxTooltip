package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipData;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Inject(at = @At("HEAD"),
      method = "Lnet/minecraft/item/ItemStack;getTooltipData()Ljava/util/Optional;",
      cancellable = true)
  private void onGetTooltipData(CallbackInfoReturnable<Optional<TooltipData>> ci) {
    PreviewContext context = PreviewContext.of((ItemStack) (Object) this,
        ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player);

    if (ShulkerBoxTooltipApi.isPreviewAvailable(context))
      ci.setReturnValue(Optional.of(new PreviewTooltipData(
          ShulkerBoxTooltipApi.getPreviewProviderForStack(context.stack()), context)));
  }

  @Inject(at = @At("RETURN"), method = "Lnet/minecraft/item/ItemStack;getTooltip"
      + "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;")
  private void onGetTooltip(PlayerEntity player, TooltipContext context,
      CallbackInfoReturnable<List<Text>> ci) {
    ShulkerBoxTooltipClient.modifyStackTooltip((ItemStack) (Object) this, ci.getReturnValue());
  }

  @Redirect(
      at = @At(value = "INVOKE",
          target = "Lnet/minecraft/nbt/NbtCompound;getType(Ljava/lang/String;)B"),
      method = "Lnet/minecraft/item/ItemStack;getTooltip"
          + "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;",
      slice = @Slice(from = @At(value = "CONSTANT", ordinal = 0, args = {"stringValue=Lore"})))
  private byte removeLore(NbtCompound tag, String key) {
    Item item = ((ItemStack) (Object) this).getItem();

    if (ShulkerBoxTooltip.config.tooltip.hideShulkerBoxLore && item instanceof BlockItem blockitem
        && blockitem.getBlock() instanceof ShulkerBoxBlock)
      return 0;
    return tag.getType(key);
  }
}
