package com.misterpemodder.shulkerboxtooltip.mixin.fabric.client;

import com.misterpemodder.shulkerboxtooltip.fabric.ShulkerBoxTooltipFabric;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.ShulkerBoxTooltipType;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {
  @Inject(at = @At("HEAD"), method =
      "Lnet/minecraft/block/ShulkerBoxBlock;appendTooltip(Lnet/minecraft/item/ItemStack;"
          + "Lnet/minecraft/world/BlockView;Ljava/util/List;"
          + "Lnet/minecraft/client/item/TooltipContext;)V", cancellable = true)
  private void onAppendTooltip(ItemStack stack, @Nullable BlockView view, List<Text> tooltip,
      TooltipContext options, CallbackInfo ci) {
    if (ShulkerBoxTooltipFabric.config != null
        && ShulkerBoxTooltipFabric.config.tooltip.type != ShulkerBoxTooltipType.VANILLA)
      ci.cancel();
  }
}
