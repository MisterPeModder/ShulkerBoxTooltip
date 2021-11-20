package com.misterpemodder.shulkerboxtooltip.impl.tooltip;

import javax.annotation.Nullable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;

public abstract class PositionAwareTooltipComponent implements TooltipComponent {
  public abstract void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices,
      ItemRenderer itemRenderer, int z, @Nullable TooltipPosition tooltipPos);

  @Override
  public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
    this.drawItems(textRenderer, x, y, matrices, itemRenderer, z, null);
  }

  public record TooltipPosition(Screen screen, int topY, int bottomY) {
  }
}
