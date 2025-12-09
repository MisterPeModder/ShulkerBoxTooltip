package com.misterpemodder.shulkerboxtooltip.impl.hook;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public interface ContainerScreenLockTooltip {
  void shulkerboxtooltip$lockTooltipPosition(GuiGraphics graphics, Font font, List<Component> text,
      Optional<TooltipComponent> data, ItemStack stack, int x, int y, Identifier backgroundTexture);
}
