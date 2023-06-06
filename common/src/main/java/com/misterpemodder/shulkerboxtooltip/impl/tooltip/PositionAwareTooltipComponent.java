package com.misterpemodder.shulkerboxtooltip.impl.tooltip;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public abstract class PositionAwareTooltipComponent implements TooltipComponent {
  public abstract void drawItemsWithTooltipPosition(TextRenderer textRenderer, int x, int y, DrawContext context,
      int tooltipTopY, int tooltipBottomY, int mouseX, int mouseY);
}
