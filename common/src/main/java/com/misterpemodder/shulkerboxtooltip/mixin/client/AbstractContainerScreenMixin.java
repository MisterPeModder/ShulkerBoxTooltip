package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.hook.ContainerScreenDrawTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.hook.ContainerScreenLockTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
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
  private Slot shulkerBoxTooltip$mouseLockSlot = null;
  @Unique
  private int shulkerBoxTooltip$mouseLockX = 0;
  @Unique
  private int shulkerBoxTooltip$mouseLockY = 0;


  @Shadow
  protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
    return null;
  }

  @Inject(at = @At("HEAD"), method = "isHovering(Lnet/minecraft/world/inventory/Slot;DD)Z", cancellable = true)
  private void forceFocusSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
    if (this.shulkerBoxTooltip$mouseLockSlot == null)
      return;

    // Handling the case where the hovered item stack get swapped for air while the tooltip is locked
    // When this happens, the lockTooltipPosition() hook will not be called (there is no tooltip for air),
    // so we need to perform cleanup logic here.
    //
    // We also need to check if the slot is still part of the handler,
    // as it may have been removed (this is the case when switching tabs in the creative inventory)

    if (!this.shulkerBoxTooltip$mouseLockSlot.hasItem() || !this.menu.slots.contains(
        this.shulkerBoxTooltip$mouseLockSlot) || !this.menu.getCarried()
        .isEmpty() // an item is carried. This may happen on bundles with right click or on behavior of other mods
    ) {
      this.shulkerBoxTooltip$mouseLockSlot = null;
      return;
    }

    cir.setReturnValue(slot == this.shulkerBoxTooltip$mouseLockSlot);
  }

  @Inject(at = @At("HEAD"), method = "extractTooltip(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V")
  private void enableLockKeyHints(CallbackInfo ci) {
    ShulkerBoxTooltipClient.setLockKeyHintsEnabled(true);
  }

  @Inject(at = @At("RETURN"), method = "extractTooltip(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V")
  private void disableLockKeyHints(CallbackInfo ci) {
    ShulkerBoxTooltipClient.setLockKeyHintsEnabled(false);
  }

  @Override
  public void shulkerboxtooltip$lockTooltipPosition(GuiGraphicsExtractor graphics, Font font, List<Component> text,
      Optional<TooltipComponent> data, ItemStack stack, int x, int y, Identifier backgroundTexture,
      Operation<Void> originalSetTooltipForNextFrame) {
    Slot mouseLockSlot = this.shulkerBoxTooltip$mouseLockSlot;

    if (ShulkerBoxTooltipClient.isLockPreviewKeyPressed()) {
      if (mouseLockSlot == null) {
        // when locking is requested and no slot is currently locked.
        mouseLockSlot = this.hoveredSlot;
        this.shulkerBoxTooltip$mouseLockX = x;
        this.shulkerBoxTooltip$mouseLockY = y;
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
        x = this.shulkerBoxTooltip$mouseLockX;
        y = this.shulkerBoxTooltip$mouseLockY;
        backgroundTexture = stack.get(DataComponents.TOOLTIP_STYLE);
      } else {
        mouseLockSlot = null;
      }
    }
    this.shulkerBoxTooltip$mouseLockSlot = mouseLockSlot;
    this.shulkerboxtooltip$renderLockedTooltip(graphics, font, text, data, stack, x, y, backgroundTexture,
        originalSetTooltipForNextFrame);
  }

  @Unique
  private void shulkerboxtooltip$renderLockedTooltip(GuiGraphicsExtractor graphics, Font font, List<Component> text,
      Optional<TooltipComponent> data, ItemStack stack, int x, int y, Identifier backgroundTexture,
      Operation<Void> originalSetTooltipForNextFrame) {
    var self = (ContainerScreenDrawTooltip) this;

    if (this.shulkerBoxTooltip$mouseLockSlot == null) {
      // When not locking, render the vanilla deferred way (1.21.6+).
      self.shulkerboxtooltip$renderTooltip(graphics, font, text, data, stack, x, y, backgroundTexture, originalSetTooltipForNextFrame);
    } else {
      // When locking, render the tooltip immediately to avoid problems when multiple tooltips are requested in the same frame.
      GuiGraphicsExtensions.renderTooltipImmediate(graphics,
          () -> self.shulkerboxtooltip$renderTooltip(graphics, font, text, data, stack, x, y, backgroundTexture, originalSetTooltipForNextFrame));
    }
  }

}
