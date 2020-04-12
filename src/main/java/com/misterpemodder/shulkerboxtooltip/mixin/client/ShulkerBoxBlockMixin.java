package com.misterpemodder.shulkerboxtooltip.mixin.client;

import java.util.List;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.ShulkerBoxTooltipType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.world.BlockView;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin extends BlockWithEntity {
  ShulkerBoxBlockMixin() {
    super(null);
  }

  @Inject(
      at = @At(value = "INVOKE_ASSIGN",
          target = "Lnet/minecraft/item/ItemStack;getSubTag"
              + "(Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;"),
      method = "Lnet/minecraft/block/ShulkerBoxBlock;buildTooltip(Lnet/minecraft/item/ItemStack;"
          + "Lnet/minecraft/world/BlockView;Ljava/util/List;"
          + "Lnet/minecraft/client/item/TooltipContext;)V",
      cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
  private void onBuildTooltip(ItemStack stack, @Nullable BlockView view, List<Text> tooltip,
      TooltipContext options, CallbackInfo ci, CompoundTag compound) {
    if (ShulkerBoxTooltip.config.main.tooltipType != ShulkerBoxTooltipType.VANILLA)
      ci.cancel();
  }
}
