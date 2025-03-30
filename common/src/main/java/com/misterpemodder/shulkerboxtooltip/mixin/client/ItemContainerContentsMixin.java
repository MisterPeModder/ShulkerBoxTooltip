package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemContainerContents.class)
public class ItemContainerContentsMixin {
  @Inject(at = @At("HEAD"), method = "addToTooltip("
                                     + "Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;"
                                     + "Lnet/minecraft/world/item/TooltipFlag;Lnet/minecraft/core/component/DataComponentGetter;)V", cancellable = true)
  void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag,
      DataComponentGetter dataComponentGetter, CallbackInfo ci) {
    if (ShulkerBoxTooltip.config != null
        && ShulkerBoxTooltip.config.tooltip.type != Configuration.ShulkerBoxTooltipType.VANILLA) {
      ci.cancel();
    }
  }
}