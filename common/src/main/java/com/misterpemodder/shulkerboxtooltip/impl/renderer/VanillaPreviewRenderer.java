package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class VanillaPreviewRenderer extends BasePreviewRenderer {
  public static final VanillaPreviewRenderer INSTANCE = new VanillaPreviewRenderer();

  private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace(
      "container/bundle/slot_highlight_back");
  private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace(
      "container/bundle/slot_highlight_front");
  private static final Identifier SLOT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace(
      "container/bundle/slot_background");

  private int lastNonEmptySlot;

  VanillaPreviewRenderer() {
    super(24, 24, 0, 0);
  }


  @Override
  protected int getMaxRowSize() {
    return Math.min(super.getMaxRowSize(), this.getInvSize());
  }

  @Override
  public int getWidth() {
    return this.getMaxRowSize() * 24;
  }

  @Override
  public int getHeight() {
    return this.getRowCount() * 24;
  }

  private int getRowCount() {
    return (int) Math.ceil((this.getInvSize()) / (double) this.getMaxRowSize());
  }

  protected int getInvSize() {
    if (this.previewType == PreviewType.COMPACT)
      return Math.max(1, this.compactItems.size());
    else
      return this.lastNonEmptySlot + 1;
  }

  public void setPreview(PreviewContext context, PreviewProvider provider) {
    super.setPreview(context, provider);
    this.lastNonEmptySlot = this.fullItems.size() - 1;
    for (; this.lastNonEmptySlot >= 0; --this.lastNonEmptySlot) {
      if (!this.fullItems.get(this.lastNonEmptySlot).isEmpty())
        break;
    }
  }

  @Override
  protected int getSlotAt(int x, int y) {
    return super.getSlotAt(x - 1, y - 1);
  }

  @Override
  public void draw(int x, int y, int viewportWidth, int viewportHeight, GuiGraphicsExtractor graphics, Font font, int mouseX,
      int mouseY) {
    if (this.compactItems.isEmpty() || this.previewType == PreviewType.NO_PREVIEW)
      return;

    x += (viewportWidth - this.getWidth()) / 2; // Align center
    this.drawSlots(x, y, graphics, font, mouseX, mouseY, this.lastNonEmptySlot);
    this.drawInnerTooltip(x, y, graphics, font, mouseX, mouseY);
  }

  @Override
  protected void drawSlot(ItemStack stack, int x, int y, GuiGraphicsExtractor graphics, Font font, int slot,
      boolean isHighlighted, boolean shortItemCount) {
    int maxRowSize = this.getMaxRowSize();
    int sx = this.slotXOffset + x + this.slotWidth * (slot % maxRowSize);
    int sy = this.slotYOffset + y + this.slotHeight * (slot / maxRowSize);

    if (isHighlighted) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, sx, sy, 24, 24);
    } else {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, sx, sy, 24, 24);
    }

    if (!stack.isEmpty())
      this.drawItem(stack, sx + 4, sy + 4, graphics, font, shortItemCount);

    if (isHighlighted) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, sx, sy, 24, 24);
    }
  }
}
