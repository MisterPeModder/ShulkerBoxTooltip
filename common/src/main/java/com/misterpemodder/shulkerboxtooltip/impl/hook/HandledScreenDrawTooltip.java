package com.misterpemodder.shulkerboxtooltip.impl.hook;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public interface HandledScreenDrawTooltip {

  void shulkerboxtooltip$drawMouseoverTooltip(DrawContext drawContext, TextRenderer textRenderer, List<Text> text,
      Optional<TooltipData> data, ItemStack stack, int x, int y);
}
