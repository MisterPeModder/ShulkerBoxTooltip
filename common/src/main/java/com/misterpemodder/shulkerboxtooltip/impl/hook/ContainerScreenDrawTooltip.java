package com.misterpemodder.shulkerboxtooltip.impl.hook;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public interface ContainerScreenDrawTooltip {
  /**
   * Adapter over the vanilla GuiGraphicsExtractor.setTooltipForNextFrame(),
   * which does not accept the "backgroundTexture" argument on Forge for some reason.
   */
  void shulkerboxtooltip$renderTooltip(GuiGraphicsExtractor graphics, Font font, List<Component> text,
      Optional<TooltipComponent> data, ItemStack stack, int x, int y, Identifier backgroundTexture, Operation<Void> original);
}
