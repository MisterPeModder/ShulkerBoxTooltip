package com.misterpemodder.shulkerboxtooltip.impl.tooltip;

import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.PreviewPosition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;

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
  public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices,
      ItemRenderer itemRenderer, int z, TextureManager textureManager,
      @Nullable TooltipPosition tooltipPos) {
    renderer.setPreview(this.context, this.provider);
    renderer.setPreviewType(
      ShulkerBoxTooltipApi.getCurrentPreviewType(this.provider.isFullPreviewAvailable(this.context)));

    PreviewPosition position = ShulkerBoxTooltip.config.preview.position;

    if (tooltipPos != null && position != PreviewPosition.INSIDE) {
      int h = this.renderer.getHeight();
      int w = this.renderer.getWidth();
      Screen screen = tooltipPos.screen();

      x = Math.min(x - 4, screen.width - w);
      y = tooltipPos.bottomY();
      if (position == PreviewPosition.OUTSIDE_TOP
          || (position == PreviewPosition.OUTSIDE && y + h > screen.height))
        y = tooltipPos.topY() - h;
    }
    this.renderer.draw(x, y, z, matrices, textRenderer, itemRenderer, textureManager);
  }
}
