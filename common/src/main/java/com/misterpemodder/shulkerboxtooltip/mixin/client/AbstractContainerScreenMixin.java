package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.hook.ContainerScreenDrawTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.hook.ContainerScreenLockTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
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

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin implements ContainerScreenLockTooltip {

  @Shadow
  @Nullable
  protected Slot hoveredSlot;

  @Final
  @Shadow
  protected AbstractContainerMenu menu;

  @Unique
  @Nullable
  private Slot mouseLockSlot = null;
  @Unique
  private int mouseLockX = 0;
  @Unique
  private int mouseLockY = 0;


  @Shadow
  protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
    return null;
  }

  @Inject(at = @At("HEAD"), method = "isHovering(Lnet/minecraft/world/inventory/Slot;DD)Z", cancellable = true)
  private void forceFocusSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
    if (this.mouseLockSlot != null) {
      // Handling the case where the hovered item stack get swapped for air while the tooltip is locked
      // When this happens, the lockTooltipPosition() hook will not be called (there is no tooltip for air),
      // so we need to perform cleanup logic here.
      //
      // We also need to check if the slot is still part of the handler,
      // as it may have been removed (this is the case when switching tabs in the creative inventory)

      if (this.mouseLockSlot.hasItem() && this.menu.slots.contains(this.mouseLockSlot))
        cir.setReturnValue(slot == this.mouseLockSlot && this.menu.getCarried().isEmpty());
      else
        // reset the lock if the stack is no longer present
        this.mouseLockSlot = null;
    }
  }

  @Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V")
  private void enableLockKeyHints(CallbackInfo ci) {
    ShulkerBoxTooltipClient.setLockKeyHintsEnabled(true);
  }

  @Inject(at = @At("RETURN"), method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V")
  private void disableLockKeyHints(CallbackInfo ci) {
    ShulkerBoxTooltipClient.setLockKeyHintsEnabled(false);
  }

  @Override
  public void shulkerboxtooltip$lockTooltipPosition(GuiGraphics graphics, Font font, List<Component> text,
      Optional<TooltipComponent> data, ItemStack stack, int x, int y, Identifier backgroundTexture) {
    Slot mouseLockSlot = this.mouseLockSlot;

    if (ShulkerBoxTooltipClient.isLockPreviewKeyPressed()) {
      if (mouseLockSlot == null) {
        // when locking is requested and no slot is currently locked.
        mouseLockSlot = this.hoveredSlot;
        this.mouseLockX = x;
        this.mouseLockY = y;
      }
    } else {
      mouseLockSlot = null;
    }

    if (mouseLockSlot != null) {
      ItemStack mouseStack = mouseLockSlot.getItem();

      PreviewContext context = PreviewContext.builder(mouseStack).withOwner(
          ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player).build();

      // Check if the locked slot contains an item that is actively being previewed,
      // if not we reset the lock, so that pressing "Control" doesn't randomly lock slots for non-previewable items.
      if (ShulkerBoxTooltipApi.isPreviewAvailable(context)) {
        // override the tooltip that would be displayed with that of the locked slot item
        text = this.getTooltipFromContainerItem(mouseStack);
        data = mouseStack.getTooltipImage();
        stack = mouseStack;
        x = this.mouseLockX;
        y = this.mouseLockY;
      } else {
        mouseLockSlot = null;
      }
    }
    this.mouseLockSlot = mouseLockSlot;
    this.shulkerboxtooltip$renderLockedTooltip(graphics, font, text, data, stack, x, y, backgroundTexture);
  }

  @Unique
  private void shulkerboxtooltip$renderLockedTooltip(GuiGraphics graphics, Font font, List<Component> text,
      Optional<TooltipComponent> data, ItemStack stack, int x, int y, Identifier backgroundTexture) {
    var self = (ContainerScreenDrawTooltip) this;

    if (this.mouseLockSlot == null) {
      // When not locking, render the vanilla deferred way (1.21.6+).
      self.shulkerboxtooltip$renderTooltip(graphics, font, text, data, stack, x, y, backgroundTexture);
    } else {
      // When locking, render the tooltip immediately to avoid problems when multiple tooltips are requested in the same frame.
      GuiGraphicsExtensions.renderTooltipImmediate(graphics,
          () -> self.shulkerboxtooltip$renderTooltip(graphics, font, text, data, stack, x, y, backgroundTexture));
    }
  }

}
