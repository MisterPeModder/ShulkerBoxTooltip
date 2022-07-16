package com.misterpemodder.shulkerboxtooltip.mixin.fabric.client;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.fabric.ShulkerBoxTooltipClientFabric;
import com.misterpemodder.shulkerboxtooltip.fabric.ShulkerBoxTooltipFabric;
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
        ShulkerBoxTooltipClientFabric.client == null ? null : ShulkerBoxTooltipClientFabric.client.player);

    if (ShulkerBoxTooltipApi.isPreviewAvailable(context))
      ci.setReturnValue(Optional.of(new PreviewTooltipData(
          ShulkerBoxTooltipApi.getPreviewProviderForStack(context.getStack()), context)));
  }

  @Inject(at = @At("RETURN"), method = "Lnet/minecraft/item/ItemStack;getTooltip"
      + "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;")
  private void onGetTooltip(PlayerEntity player, TooltipContext context,
      CallbackInfoReturnable<List<Text>> ci) {
    ShulkerBoxTooltipClientFabric.modifyStackTooltip((ItemStack) (Object) this, ci.getReturnValue());
  }

  @Redirect(
      at = @At(value = "INVOKE",
          target = "Lnet/minecraft/nbt/NbtCompound;getType(Ljava/lang/String;)B"),
      method = "Lnet/minecraft/item/ItemStack;getTooltip"
          + "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;",
      slice = @Slice(from = @At(value = "CONSTANT", ordinal = 0, args = {"stringValue=Lore"})))
  private byte removeLore(NbtCompound tag, String key) {
    Item item = ((ItemStack) (Object) this).getItem();

    if (ShulkerBoxTooltipFabric.config.tooltip.hideShulkerBoxLore && item instanceof BlockItem blockitem
        && blockitem.getBlock() instanceof ShulkerBoxBlock)
      return 0;
    return tag.getType(key);
  }
}
