package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

  @Shadow
  @Nullable
  protected Slot focusedSlot;

  @Final
  @Shadow
  protected ScreenHandler handler;

  @Unique
  @Nullable
  private Slot mouseLockSlot = null;
  @Unique
  private int mouseLockX = 0;
  @Unique
  private int mouseLockY = 0;


  @Shadow
  protected List<Text> getTooltipFromItem(ItemStack stack) {
    return null;
  }

  @Inject(at = @At("HEAD"), method = "isPointOverSlot(Lnet/minecraft/screen/slot/Slot;DD)Z", cancellable = true)
  private void forceFocusSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
    if (this.mouseLockSlot != null) {
      if (this.mouseLockSlot.hasStack())
        cir.setReturnValue(slot == this.mouseLockSlot && this.handler.getCursorStack().isEmpty());
      else
        this.mouseLockSlot = null;
    }
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"), method = "drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V")
  private void lockTooltipPosition(DrawContext drawContext, TextRenderer textRenderer, List<Text> text,
      Optional<TooltipData> data, int x, int y) {
    if (ShulkerBoxTooltipClient.isLockPreviewKeyPressed()) {
      if (this.mouseLockSlot == null) {
        this.mouseLockSlot = this.focusedSlot;
        this.mouseLockX = x;
        this.mouseLockY = y;
      }
    } else {
      this.mouseLockSlot = null;
    }

    if (this.mouseLockSlot != null) {
      ItemStack stack = this.mouseLockSlot.getStack();

      PreviewContext context = PreviewContext.of(stack,
          ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player);

      if (ShulkerBoxTooltipApi.isPreviewAvailable(context)) {
        text = this.getTooltipFromItem(stack);
        data = stack.getTooltipData();
        x = this.mouseLockX;
        y = this.mouseLockY;
      } else {
        this.mouseLockSlot = null;
        this.focusedSlot = null;
      }
    }

    drawContext.drawTooltip(textRenderer, text, data, x, y);
  }
}
