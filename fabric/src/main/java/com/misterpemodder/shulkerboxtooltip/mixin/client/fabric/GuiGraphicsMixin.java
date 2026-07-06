package com.misterpemodder.shulkerboxtooltip.mixin.client.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsMixin {

  @WrapOperation(at = @At(value = "INVOKE", target =
      "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;"
      + "positionTooltip(IIIIII)Lorg/joml/Vector2ic;"), method = "tooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/Identifier;)V", require = 0)
  private Vector2ic captureTooltipYPosition(ClientTooltipPositioner positioner, int guiWidth, int guiHeight, int x,
      int y, int totalWidth, int totalHeight, Operation<Vector2ic> original) {
    Vector2ic result = original.call(positioner, guiWidth, guiHeight, x, y, totalWidth, totalHeight);
    var extendedGraphics = (GuiGraphicsExtensions) this;
    extendedGraphics.setTooltipTopXPosition(result.x());
    extendedGraphics.setTooltipTopYPosition(result.y());
    return result;
  }

}
