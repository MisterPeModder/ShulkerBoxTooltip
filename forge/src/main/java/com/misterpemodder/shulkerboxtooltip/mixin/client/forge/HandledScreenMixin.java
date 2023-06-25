package com.misterpemodder.shulkerboxtooltip.mixin.client.forge;

import com.misterpemodder.shulkerboxtooltip.impl.hook.HandledScreenDrawTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.hook.HandledScreenLockTooltip;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@Mixin(HandledScreen.class)
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class HandledScreenMixin implements HandledScreenDrawTooltip {
  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;renderTooltip("
      + "Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;Lnet/minecraft/item/ItemStack;II)V"), method = "drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V")
  private void lockTooltipPosition(DrawContext drawContext, TextRenderer textRenderer, List<Text> text,
      Optional<TooltipData> data, ItemStack stack, int x, int y) {
    var self = (HandledScreenLockTooltip) this;
    self.shulkerboxtooltip$lockTooltipPosition(drawContext, textRenderer, text, data, stack, x, y);
  }

  @Override
  public void shulkerboxtooltip$drawMouseoverTooltip(@Nonnull DrawContext drawContext, TextRenderer textRenderer,
      List<Text> text, Optional<TooltipData> data, ItemStack stack, int x, int y) {
    drawContext.renderTooltip(textRenderer, text, data, stack, x, y);
  }
}
