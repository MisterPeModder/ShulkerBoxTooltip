package com.misterpemodder.shulkerboxtooltip.impl.tooltip;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.PreviewPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class PreviewTooltipComponent extends PositionAwareTooltipComponent {
  private final PreviewRenderer renderer;
  private final PreviewProvider provider;
  private final PreviewContext context;

  public PreviewTooltipComponent(PreviewTooltipData data) {
    PreviewRenderer renderer = data.provider().getRenderer();

    if (renderer == null)
      renderer = PreviewRenderer.getDefaultRendererInstance();
    this.renderer = renderer;
    this.provider = data.provider();
    this.context = data.context();
  }

  @Override
  public int getHeight() {
    if (ShulkerBoxTooltip.config.preview.position == PreviewPosition.INSIDE)
      return this.renderer.getHeight() + 2 + 4;
    return 0;
  }

  @Override
  public int getWidth(TextRenderer textRenderer) {
    if (ShulkerBoxTooltip.config.preview.position == PreviewPosition.INSIDE)
      return this.renderer.getWidth() + 2;
    return 0;
  }

  @Override
  public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
    this.prepareRenderer();
    this.drawAt(x, y, context, textRenderer);
  }

  @Override
  public void drawItemsWithTooltipPosition(TextRenderer textRenderer, int x, int y, DrawContext context,
      int tooltipTopY, int tooltipBottomY) {
    PreviewPosition position = ShulkerBoxTooltip.config.preview.position;

    this.prepareRenderer();
    if (position != PreviewPosition.INSIDE) {
      int h = this.renderer.getHeight();
      int w = this.renderer.getWidth();
      int screenW = context.getScaledWindowWidth();
      int screenH = context.getScaledWindowHeight();

      x = Math.min(x - 4, screenW - w);
      y = tooltipBottomY;
      if (position == PreviewPosition.OUTSIDE_TOP || (position == PreviewPosition.OUTSIDE && y + h > screenH))
        y = tooltipTopY - h;
    }
    this.drawAt(x, y, context, textRenderer);
  }

  private void prepareRenderer() {
    this.renderer.setPreview(this.context, this.provider);
    this.renderer.setPreviewType(
        ShulkerBoxTooltipApi.getCurrentPreviewType(this.provider.isFullPreviewAvailable(this.context)));
  }

  private void drawAt(int x, int y, DrawContext context, TextRenderer textRenderer) {
    this.renderer.draw(x, y, context, textRenderer, MinecraftClient.getInstance().getTextureManager());
  }
}
