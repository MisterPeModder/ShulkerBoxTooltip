package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.hook.DrawContextExtensions;
import com.misterpemodder.shulkerboxtooltip.impl.hook.HandledScreenDrawTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.hook.HandledScreenLockTooltip;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mixin(HandledScreen.class)
public class HandledScreenMixin implements HandledScreenLockTooltip {

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
      // Handling the case where the hovered item stack get swapped for air while the tooltip is locked
      // When this happens, the lockTooltipPosition() hook will not be called (there is no tooltip for air),
      // so we need to perform cleanup logic here.
      //
      // We also need to check if the slot is still part of the handler,
      // as it may have been removed (this is the case when switching tabs in the creative inventory)

      if (this.mouseLockSlot.hasStack() && this.handler.slots.contains(this.mouseLockSlot))
        cir.setReturnValue(slot == this.mouseLockSlot && this.handler.getCursorStack().isEmpty());
      else
        // reset the lock if the stack is no longer present
        this.mouseLockSlot = null;
    }
  }

  /**
   * Makes the current mouse position available via extensions to the DrawContext.
   */
  @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V")
  private void captureMousePosition(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    DrawContextExtensions extensions = (DrawContextExtensions) context;
    extensions.setMouseY(mouseY);
    extensions.setMouseX(mouseX);
  }


  @Inject(at = @At("HEAD"), method = "drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V")
 private void enableLockKeyHints(CallbackInfo ci) {
    ShulkerBoxTooltipClient.setLockKeyHintsEnabled(true);
 }

  @Inject(at = @At("RETURN"), method = "drawMouseoverTooltip(Lnet/minecraft/client/gui/DrawContext;II)V")
  private void disableLockKeyHints(CallbackInfo ci) {
    ShulkerBoxTooltipClient.setLockKeyHintsEnabled(false);
  }

  @Override
  public void shulkerboxtooltip$lockTooltipPosition(DrawContext drawContext, TextRenderer textRenderer, List<Text> text,
      Optional<TooltipData> data, ItemStack stack, int x, int y) {
    Slot mouseLockSlot = this.mouseLockSlot;

    if (ShulkerBoxTooltipClient.isLockPreviewKeyPressed()) {
      if (mouseLockSlot == null) {
        // when locking is requested and no slot is currently locked.
        mouseLockSlot = this.focusedSlot;
        this.mouseLockX = x;
        this.mouseLockY = y;
      }
    } else {
      mouseLockSlot = null;
    }

    if (mouseLockSlot != null) {
      ItemStack mouseStack = mouseLockSlot.getStack();

      PreviewContext context = PreviewContext.of(mouseStack,
          ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player);

      // Check if the locked slot contains an item that is actively being previewed,
      // if not we reset the lock, so that pressing "Control" doesn't randomly lock slots for non-previewable items.
      if (ShulkerBoxTooltipApi.isPreviewAvailable(context)) {
        // override the tooltip that would be displayed with that of the locked slot item
        text = this.getTooltipFromItem(mouseStack);
        data = mouseStack.getTooltipData();
        stack = mouseStack;
        x = this.mouseLockX;
        y = this.mouseLockY;
      } else {
        mouseLockSlot = null;
      }
    }
    this.mouseLockSlot = mouseLockSlot;

    var self = (HandledScreenDrawTooltip) this;
    self.shulkerboxtooltip$drawMouseoverTooltip(drawContext, textRenderer, text, data, stack, x, y);
  }

}