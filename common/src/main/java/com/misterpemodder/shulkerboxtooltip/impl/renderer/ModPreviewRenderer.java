package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import static com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil.id;

@Environment(EnvType.CLIENT)
public class ModPreviewRenderer extends BasePreviewRenderer {
  public static final ModPreviewRenderer INSTANCE = new ModPreviewRenderer();

  private static final Identifier DEFAULT_TEXTURE_LIGHT = id("shulker_box_tooltip");
  private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace(
      "container/slot_highlight_back");
  private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace(
      "container/slot_highlight_front");

  ModPreviewRenderer() {
    super(18, 18, 8, 8);
  }

  @Override
  public int getWidth() {
    return 14 + Math.min(this.getMaxRowSize(), this.getInvSize()) * 18;
  }

  @Override
  public int getHeight() {
    return 14 + (int) Math.ceil(this.getInvSize() / (double) this.getMaxRowSize()) * 18;
  }

  private int getInvSize() {
    return this.previewType == PreviewType.COMPACT ?
        Math.max(1, this.compactItems.size()) :
        this.provider.getInventoryMaxSize(this.previewContext);
  }

  /**
   * Sets the color of the preview window.
   */
  private int getColor() {
    ColorKey key;

    if (this.config.useColors()) {
      key = this.provider.getWindowColorKey(this.previewContext);
    } else {
      key = ColorKey.DEFAULT;
    }
    return 0xFF000000 | key.rgb();
  }

  private Identifier getTexture() {
    if (this.textureOverride != null)
      return this.textureOverride;
    return DEFAULT_TEXTURE_LIGHT;
  }

  private void drawBackground(int x, int y, GuiGraphics graphics) {
    int invSize = this.getInvSize();
    int slotSize = 18;
    int rows = Math.min(this.getMaxRowSize(), invSize);
    int cols = (int) Math.ceil(invSize / (double) rows);

    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.getTexture(), x, y, 14 + rows * slotSize,
        14 + cols * slotSize, this.getColor());
  }

  @Override
  public void draw(int x, int y, int viewportWidth, int viewportHeight, GuiGraphics graphics, Font font, int mouseX,
      int mouseY) {
    if (this.compactItems.isEmpty() || this.previewType == PreviewType.NO_PREVIEW)
      return;
    this.drawBackground(x, y, graphics);
    this.drawSlots(x, y, graphics, font, mouseX, mouseY, Integer.MAX_VALUE);
    this.drawInnerTooltip(x, y, graphics, font, mouseX, mouseY);
  }

  @Override
  protected void drawSlot(ItemStack stack, int x, int y, GuiGraphics graphics, Font font, int slot,
      boolean isHighlighted, boolean shortItemCount) {
    int maxRowSize = this.getMaxRowSize();
    int sx = this.slotXOffset + x + this.slotWidth * (slot % maxRowSize);
    int sy = this.slotYOffset + y + this.slotHeight * (slot / maxRowSize);

    if (isHighlighted) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, sx - 4, sy - 4, 24, 24);
    }

    if (!stack.isEmpty())
      this.drawItem(stack, sx, sy, graphics, font, shortItemCount);

    if (isHighlighted) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, sx - 4, sy - 4, 24, 24);
    }
  }
}
