package com.misterpemodder.shulkerboxtooltip.mixin.client.forge;

import com.misterpemodder.shulkerboxtooltip.impl.hook.ContainerScreenDrawTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.hook.ContainerScreenLockTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@Mixin(AbstractContainerScreen.class)
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class AbstractContainerScreenMixin implements ContainerScreenDrawTooltip {
  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;Lnet/minecraft/world/item/ItemStack;II)V"), method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V")
  private void lockTooltipPosition(GuiGraphics graphics, Font font, List<Component> text,
      Optional<TooltipComponent> data, ItemStack stack, int x, int y) {
    var self = (ContainerScreenLockTooltip) this;
    self.shulkerboxtooltip$lockTooltipPosition(graphics, font, text, data, stack, x, y,
        stack.get(DataComponents.TOOLTIP_STYLE));
  }

  @Override
  public void shulkerboxtooltip$renderTooltip(@Nonnull GuiGraphics graphics, Font font, List<Component> text,
      Optional<TooltipComponent> image, ItemStack stack, int x, int y, Identifier backgroundTexture) {
    GuiGraphicsExtensions.renderTooltipImmediate(graphics,
        () -> graphics.setTooltipForNextFrame(font, text, image, stack, x, y));
  }
}
