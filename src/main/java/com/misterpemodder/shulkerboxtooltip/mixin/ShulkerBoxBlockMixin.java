package com.misterpemodder.shulkerboxtooltip.mixin;

import java.util.List;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.world.BlockView;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin extends BlockWithEntity {
  ShulkerBoxBlockMixin() {
    super(null);
  }

  @Inject(
      at = @At(value = "INVOKE_ASSIGN",
          target = "Lnet/minecraft/item/ItemStack;getSubCompoundTag"
              + "(Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;"),
      method = "Lnet/minecraft/block/ShulkerBoxBlock;buildTooltip(Lnet/minecraft/item/ItemStack;"
          + "Lnet/minecraft/world/BlockView;Ljava/util/List;"
          + "Lnet/minecraft/client/item/TooltipOptions;)V",
      cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
  private void onBuildTooltip(ItemStack stack, @Nullable BlockView view,
      List<TextComponent> tooltip, TooltipOptions options, CallbackInfo ci,
      CompoundTag compoundTag_1) {
    ShulkerBoxTooltip.buildShulkerBoxTooltip(stack, view, tooltip, options, compoundTag_1);
    ci.cancel();
  }
}
