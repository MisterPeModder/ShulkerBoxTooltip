package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VanillaPreviewRenderer extends BasePreviewRenderer {
  public static final Identifier DEFAULT_TEXTURE = new Identifier("container/bundle/background");
  public static final VanillaPreviewRenderer INSTANCE = new VanillaPreviewRenderer();

  VanillaPreviewRenderer() {
    super(18, 20, 2, 2);
  }

  @Override
  public int getWidth() {
    return this.getMaxRowSize() * 18;
  }

  private int getColumnsWidth() {
    return this.getColumnCount() * 18 + 2;
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
  public void draw(int x, int y, DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY) {
    ++y;
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    RenderSystem.enableDepthTest();

    Identifier texture = this.getTexture();

    context.drawGuiTexture(texture, x, y, this.getColumnsWidth(), this.getHeight());

    var sprite = BundleTooltipComponent.Sprite.SLOT;
    for (int slotY = 0; slotY < this.getRowCount(); ++slotY) {
      for (int slotX = 0; slotX < this.getColumnCount(); ++slotX) {
        int px = x + slotX * 18 + 1;
        int py = y + slotY * 20 + 1;
        context.drawGuiTexture(sprite.texture, px, py, 0, sprite.width, sprite.height);
      }
    }

    this.drawItems(x, y, context, textRenderer);
    this.drawInnerTooltip(x, y, context, textRenderer, mouseX, mouseY);
  }

  private Identifier getTexture() {
    if (this.textureOverride == null)
      return DEFAULT_TEXTURE;
    else
      return this.textureOverride;
  }
}
