package com.misterpemodder.shulkerboxtooltip.mixin.client.fabric;

import com.misterpemodder.shulkerboxtooltip.impl.hook.HandledScreenDrawTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.hook.HandledScreenLockTooltip;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mixin(HandledScreen.class)
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class HandledScreenMixin implements HandledScreenDrawTooltip {
  @Shadow
  @Nullable
  protected Slot focusedSlot;

  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"), method = "drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V")
  private void lockTooltipPosition(DrawContext drawContext, TextRenderer textRenderer, List<Text> text,
      Optional<TooltipData> data, int x, int y) {
    ItemStack stack = this.focusedSlot == null ? null : this.focusedSlot.getStack();
    var self = (HandledScreenLockTooltip) this;
    self.shulkerboxtooltip$lockTooltipPosition(drawContext, textRenderer, text, data, stack, x, y);
  }

  @Override
  public void shulkerboxtooltip$drawMouseoverTooltip(@Nonnull DrawContext drawContext, TextRenderer textRenderer,
      List<Text> text, Optional<TooltipData> data, ItemStack stack, int x, int y) {
    drawContext.drawTooltip(textRenderer, text, data, x, y);
  }
}
