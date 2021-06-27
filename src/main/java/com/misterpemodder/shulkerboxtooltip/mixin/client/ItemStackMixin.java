package com.misterpemodder.shulkerboxtooltip.mixin.client;

import java.util.List;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Inject(at = @At("RETURN"),
      method = "Lnet/minecraft/item/ItemStack;getTooltip"
          + "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)"
          + "Ljava/util/List;")
  private void onGetTooltip(PlayerEntity player, TooltipContext context,
      CallbackInfoReturnable<List<Text>> ci) {
    ShulkerBoxTooltipClient.modifyStackTooltip((ItemStack) (Object) this, ci.getReturnValue());
  }

  @Redirect(
      at = @At(value = "INVOKE",
          target = "Lnet/minecraft/nbt/CompoundTag;contains(Ljava/lang/String;I)Z", ordinal = 0),
      method = "Lnet/minecraft/item/ItemStack;getTooltip"
          + "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)"
          + "Ljava/util/List;",
      slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=display", ordinal = 0)))
  private boolean removeLore(CompoundTag tag, String key, int type, @Nullable PlayerEntity player,
      TooltipContext context) {
    Item item = ((ItemStack) (Object) this).getItem();

    if (ShulkerBoxTooltip.config.main.hideShulkerBoxLore && item instanceof BlockItem
        && ((BlockItem) item).getBlock() instanceof ShulkerBoxBlock)
      return false;
    return tag.contains(key, type);
  }
}
