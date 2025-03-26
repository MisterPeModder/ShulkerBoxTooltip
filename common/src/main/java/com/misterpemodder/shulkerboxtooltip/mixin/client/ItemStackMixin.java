package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Inject(at = @At("HEAD"), method = "addToTooltip", cancellable = true)
  private <T extends TooltipProvider> void removeLore(DataComponentType<T> componentType, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag, CallbackInfo ci) {
    if (componentType == DataComponents.LORE) {
      Item item = ((ItemStack) (Object) this).getItem();

      //noinspection UnreachableCode
      if (ShulkerBoxTooltip.config != null && ShulkerBoxTooltip.config.tooltip.hideShulkerBoxLore
          && item instanceof BlockItem blockitem && blockitem.getBlock() instanceof ShulkerBoxBlock) {
        ci.cancel();
      }
    }
  }
}
