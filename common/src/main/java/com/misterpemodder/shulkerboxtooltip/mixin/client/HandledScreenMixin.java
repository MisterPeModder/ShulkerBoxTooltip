package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.DrawContext;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.DrawContextAccess;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {


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
  @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V")
  private void captureMousePosition(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    DrawContext context = ((DrawContextAccess) this).getDrawContext();
    context.setMouseY(mouseY);
    context.setMouseX(mouseX);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V"), method = "drawMouseoverTooltip(Lnet/minecraft/client/util/math/MatrixStack;II)V")
  private void lockTooltipPosition(HandledScreen<?> instance, MatrixStack matrixStack, ItemStack itemStack, int x,
      int y) {
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
      ItemStack stack = mouseLockSlot.getStack();

      PreviewContext context = PreviewContext.of(stack,
          ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player);

      // Check if the locked slot contains an item that is actively being previewed,
      // if not we reset the lock, so that pressing "Control" doesn't randomly lock slots for non-previewable items.
      if (ShulkerBoxTooltipApi.isPreviewAvailable(context)) {
        // override the tooltip that would be displayed with that of the locked slot item
        x = this.mouseLockX;
        y = this.mouseLockY;
      } else {
        mouseLockSlot = null;
      }
    }
    this.mouseLockSlot = mouseLockSlot;

    try {
      ShulkerBoxTooltipClient.setLockKeyHintsEnabled(true);
      instance.renderTooltip(matrixStack, itemStack, x, y);
    } finally {
      ShulkerBoxTooltipClient.setLockKeyHintsEnabled(false);
    }
  }
}
