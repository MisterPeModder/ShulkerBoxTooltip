package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;getType(Ljava/lang/String;)B"), method =
      "Lnet/minecraft/item/ItemStack;getTooltip"
          + "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)"
          + "Ljava/util/List;", slice = @Slice(from = @At(value = "CONSTANT", ordinal = 0, args = {
      "stringValue=Lore"})))
  private byte removeLore(NbtCompound tag, String key) {
    //noinspection DataFlowIssue
    Item item = ((ItemStack) (Object) this).getItem();

    if (ShulkerBoxTooltip.config != null && ShulkerBoxTooltip.config.tooltip.hideShulkerBoxLore
        && item instanceof BlockItem blockitem && blockitem.getBlock() instanceof ShulkerBoxBlock)
      return 0;
    return tag.getType(key);
  }
}
