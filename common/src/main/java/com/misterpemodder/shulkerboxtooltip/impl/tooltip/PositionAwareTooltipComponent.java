package com.misterpemodder.shulkerboxtooltip.impl.tooltip;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

import javax.annotation.Nullable;

public abstract class PositionAwareTooltipComponent implements TooltipComponent {
  public abstract void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices,
      ItemRenderer itemRenderer, @Nullable TooltipPosition tooltipPos);

  @Override
  public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer) {
    this.drawItems(textRenderer, x, y, matrices, itemRenderer, null);
  }

  public record TooltipPosition(Screen screen, int topY, int bottomY) {
  }
}
