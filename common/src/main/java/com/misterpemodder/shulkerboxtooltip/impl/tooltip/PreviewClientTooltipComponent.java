package com.misterpemodder.shulkerboxtooltip.impl.tooltip;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.PreviewPosition;
import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.RenderContextImpl;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jetbrains.annotations.NotNull;

public class PreviewClientTooltipComponent implements ClientTooltipComponent {
  private final PreviewRenderer renderer;

  public PreviewClientTooltipComponent(PreviewTooltipComponent data) {
    PreviewRenderer renderer = data.provider().getRenderer();

    if (renderer == null)
      renderer = PreviewRenderer.getDefaultRendererInstance();
    this.renderer = renderer;
    PreviewProvider provider = data.provider();
    PreviewContext context = data.context();

    renderer.setPreview(context, provider);
    renderer.setPreviewType(ShulkerBoxTooltipApi.getCurrentPreviewType(provider.isFullPreviewAvailable(context)));
  }

  @Override
  public int getHeight(@NotNull Font font) {
    if (ShulkerBoxTooltip.config.preview.position == PreviewPosition.INSIDE)
      return this.renderer.getHeight() + 4;
    return 0;
  }

  @Override
  public int getWidth(@NotNull Font font) {
    if (ShulkerBoxTooltip.config.preview.position == PreviewPosition.INSIDE)
      return this.renderer.getWidth();
    return 0;
  }

  @Override
  public void extractImage(@NotNull Font font, int x, int y, int totalWidth, int totalHeight,
      @NotNull GuiGraphicsExtractor graphics) {
    var extendedGraphics = (GuiGraphicsExtensions) graphics;
    int mouseX = extendedGraphics.getMouseX();
    int mouseY = extendedGraphics.getMouseY();
    int tooltipTopX = extendedGraphics.getTooltipTopXPosition();
    int tooltipTopY = extendedGraphics.getTooltipTopYPosition();

    PreviewPosition position = ShulkerBoxTooltip.config.preview.position;
    int viewportHeight = this.renderer.getHeight();

    if (tooltipTopY == Integer.MIN_VALUE)
      // Fall back to "inside" if the tooltip Y position was not captured
      position = PreviewPosition.INSIDE;

    if (position == PreviewPosition.OUTSIDE) {
      int screenH = graphics.guiHeight();
      position = tooltipTopY + totalHeight + viewportHeight > screenH ?
          PreviewPosition.OUTSIDE_TOP :
          PreviewPosition.OUTSIDE_BOTTOM;
    }

    if (position == PreviewPosition.OUTSIDE_TOP) {
      x -= 4;
      y = tooltipTopY - viewportHeight - 4;
    } else if (position == PreviewPosition.OUTSIDE_BOTTOM) {
      x -= 4;
      y = tooltipTopY + totalHeight + 4;
    }

    this.renderer.draw(
        new RenderContextImpl(x, y, totalWidth, viewportHeight, graphics, font, mouseX, mouseY, tooltipTopX,
            tooltipTopY));
  }
}
