package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PositionAwareTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

import javax.annotation.Nullable;

/**
 * Polyfill for the DrawContext class of Minecraft 1.20.
 */
public final class DrawContext implements DrawContextExtensions {
  @Nullable
  private final Screen screen;
  private MatrixStack matrices;
  private ItemRenderer itemRenderer;
  private int tooltipTopYPosition = 0;
  private int tooltipBottomYPosition = 0;
  private int mouseX = 0;
  private int mouseY = 0;

  public DrawContext(@Nullable Screen screen) {
    this.screen = screen;
  }

  public int getScaledWindowWidth() {
    return this.screen == null ? 0 : this.screen.width;
  }

  public int getScaledWindowHeight() {
    return this.screen == null ? 0 : this.screen.height;
  }

  public MatrixStack getMatrices() {
    return this.matrices;
  }

  public ItemRenderer getItemRenderer() {
    return this.itemRenderer;
  }

  public void update(MatrixStack matrices, ItemRenderer itemRenderer) {
    this.matrices = matrices;
    this.itemRenderer = itemRenderer;
  }

  public void drawItems(TooltipComponent component, TextRenderer textRenderer, int x, int y) {
    if (component instanceof PositionAwareTooltipComponent posAwareComponent) {
      //noinspection ConstantConditions
      posAwareComponent.drawItemsWithTooltipPosition(textRenderer, x, y, this, this.getTooltipTopYPosition(),
          this.getTooltipBottomYPosition(), this.getMouseX(), this.getMouseY());
    } else
      component.drawItems(textRenderer, x, y, this.matrices, this.itemRenderer);
  }

  @Override
  public int getTooltipTopYPosition() {
    return this.tooltipTopYPosition;
  }

  @Override
  public void setTooltipTopYPosition(int tooltipTopYPosition) {
    this.tooltipTopYPosition = tooltipTopYPosition;
  }

  @Override
  public int getTooltipBottomYPosition() {
    return this.tooltipBottomYPosition;
  }

  @Override
  public void setTooltipBottomYPosition(int tooltipBottomYPosition) {
    this.tooltipBottomYPosition = tooltipBottomYPosition;
  }

  @Override
  public void setMouseX(int mouseX) {
    this.mouseX = mouseX;
  }

  @Override
  public int getMouseX() {
    return this.mouseX;
  }

  @Override
  public void setMouseY(int mouseY) {
    this.mouseY = mouseY;
  }

  @Override
  public int getMouseY() {
    return this.mouseY;
  }

  @Override
  @Nullable
  public Screen getScreen() {
    return this.screen;
  }
}
