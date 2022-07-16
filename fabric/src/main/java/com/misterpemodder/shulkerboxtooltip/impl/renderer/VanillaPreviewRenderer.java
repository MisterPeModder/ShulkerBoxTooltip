package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VanillaPreviewRenderer extends BasePreviewRenderer {
  public static final Identifier DEFAULT_TEXTURE =
      new Identifier("textures/gui/container/bundle.png");
  public static final VanillaPreviewRenderer INSTANCE = new VanillaPreviewRenderer();

  VanillaPreviewRenderer() {
    super(18, 20, 2, 2);
  }

  @Override
  public int getWidth() {
    return this.getMaxRowSize() * 18 + 2;
  }

  @Override
  public int getHeight() {
    return this.getRowCount() * 20 + 3;
  }

  private int getColumnCount() {
    return Math.min(this.getMaxRowSize(), this.getInvSize());
  }

  private int getRowCount() {
    return (int) Math.ceil(((double) getInvSize()) / (double) this.getMaxRowSize());
  }

  @Override
  public void draw(int x, int y, int z, MatrixStack matrices, TextRenderer textRenderer,
      ItemRenderer itemRenderer, TextureManager textureManager) {
    ++y;
    setTexture();
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    RenderSystem.enableDepthTest();
    this.drawBackground(x, y, z, this.getColumnCount(), this.getRowCount(), matrices);
    this.drawItems(x, y, z, textRenderer, itemRenderer);
  }

  private void drawBackground(int x, int y, int z, int columns, int rows, MatrixStack matrices) {
    for (int row = 0; row < rows; ++row) {
      for (int col = 0; col < columns; ++col) {
        this.drawSprite(matrices, 1 + x + 18 * col, 1 + y + 20 * row, z,
            BundleTooltipComponent.Sprite.SLOT);
      }
    }
    this.drawSprite(matrices, x, y, z, BundleTooltipComponent.Sprite.BORDER_CORNER_TOP);
    this.drawSprite(matrices, x + columns * 18 + 1, y, z,
        BundleTooltipComponent.Sprite.BORDER_CORNER_TOP);
    for (int col = 0; col < columns; ++col) {
      this.drawSprite(matrices, x + 1 + col * 18, y, z,
          BundleTooltipComponent.Sprite.BORDER_HORIZONTAL_TOP);
      this.drawSprite(matrices, x + 1 + col * 18, y + rows * 20, z,
          BundleTooltipComponent.Sprite.BORDER_HORIZONTAL_BOTTOM);
    }
    for (int row = 0; row < rows; ++row) {
      this.drawSprite(matrices, x, y + row * 20 + 1, z,
          BundleTooltipComponent.Sprite.BORDER_VERTICAL);
      this.drawSprite(matrices, x + columns * 18 + 1, y + row * 20 + 1, z,
          BundleTooltipComponent.Sprite.BORDER_VERTICAL);
    }
    this.drawSprite(matrices, x, y + rows * 20, z,
        BundleTooltipComponent.Sprite.BORDER_CORNER_BOTTOM);
    this.drawSprite(matrices, x + columns * 18 + 1, y + rows * 20, z,
        BundleTooltipComponent.Sprite.BORDER_CORNER_BOTTOM);
  }

  private void setTexture() {
    if (this.textureOverride == null)
      RenderSystem.setShaderTexture(0, DEFAULT_TEXTURE);
    else
      RenderSystem.setShaderTexture(0, this.textureOverride);
  }

  private void drawSprite(MatrixStack matrices, int x, int y, int z,
      BundleTooltipComponent.Sprite sprite) {
    DrawableHelper.drawTexture(matrices, x, y, z, sprite.u, sprite.v, sprite.width, sprite.height,
        128, 128);
  }
}
