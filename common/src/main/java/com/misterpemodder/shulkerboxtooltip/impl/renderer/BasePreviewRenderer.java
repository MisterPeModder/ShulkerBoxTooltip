package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.provider.EmptyPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.CompactPreviewNbtBehavior;
import com.misterpemodder.shulkerboxtooltip.impl.util.MergedItemStack;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePreviewRenderer implements PreviewRenderer {
  protected PreviewType previewType;
  protected int compactMaxRowSize;
  protected int maxRowSize;
  protected Identifier textureOverride;
  protected PreviewProvider provider;
  protected List<MergedItemStack> items;
  protected PreviewContext previewContext;

  private final int slotWidth;
  private final int slotHeight;
  private final int slotXOffset;
  private final int slotYOffset;

  protected BasePreviewRenderer(int slotWidth, int slotHeight, int slotXOffset, int slotYOffset) {
    this.items = new ArrayList<>();
    this.previewType = PreviewType.FULL;
    this.maxRowSize = 9;

    this.slotWidth = slotWidth;
    this.slotHeight = slotHeight;
    this.slotXOffset = slotXOffset;
    this.slotYOffset = slotYOffset;

    this.setPreview(PreviewContext.of(ItemStack.EMPTY), EmptyPreviewProvider.INSTANCE);
  }

  protected int getMaxRowSize() {
    return this.previewType == PreviewType.COMPACT ? this.compactMaxRowSize : this.maxRowSize;
  }

  protected int getInvSize() {
    return this.previewType == PreviewType.COMPACT ?
      Math.max(1, this.items.size()) :
      this.provider.getInventoryMaxSize(this.previewContext);
  }

  @Override
  public void setPreviewType(PreviewType type) {
    this.previewType = type;
  }

  @Override
  public void setPreview(PreviewContext context, PreviewProvider provider) {
    List<ItemStack> inventory = provider.getInventory(context);
    int rowSize = provider.getMaxRowSize(context);

    this.compactMaxRowSize = ShulkerBoxTooltip.config.preview.defaultMaxRowSize;
    if (this.compactMaxRowSize <= 0)
      this.compactMaxRowSize = 9;
    if (rowSize <= 0)
      rowSize = this.compactMaxRowSize;
    this.maxRowSize = rowSize;
    this.textureOverride = provider.getTextureOverride(context);
    this.provider = provider;
    this.items = MergedItemStack.mergeInventory(inventory, provider.getInventoryMaxSize(context),
      ShulkerBoxTooltip.config.preview.compactPreviewNbtBehavior
        != CompactPreviewNbtBehavior.SEPARATE);
    this.previewContext = context;
  }

  private void drawItem(MatrixStack matrices, ItemStack stack, int x, int y, TextRenderer textRenderer,
    ItemRenderer itemRenderer, int slot, boolean shortItemCount) {
    String countLabel = "";
    int maxRowSize = this.getMaxRowSize();

    // stack size might exceed the maximum, so we create our own count label instead of the default
    if (stack.getCount() != 1) {
      if (shortItemCount)
        countLabel = ShulkerBoxTooltipUtil.abbreviateInteger(stack.getCount());
      else
        countLabel = String.valueOf(stack.getCount());
    }

    x = this.slotXOffset + x + this.slotWidth * (slot % maxRowSize);
    y = this.slotYOffset + y + this.slotHeight * (slot / maxRowSize);

    itemRenderer.renderInGuiWithOverrides(matrices, stack , x, y);
    itemRenderer.renderGuiItemOverlay(matrices, textRenderer, stack, x, y, countLabel);
  }

  protected void drawItems(MatrixStack matrices, int x, int y, TextRenderer textRenderer,
    ItemRenderer itemRenderer) {
      
    try {
      if (this.previewType == PreviewType.COMPACT) {
        boolean shortItemCounts = ShulkerBoxTooltip.config.preview.shortItemCounts;

        for (int slot = 0, size = this.items.size(); slot < size; ++slot) {
          this.drawItem(matrices, this.items.get(slot).get(), x, y, textRenderer, itemRenderer, slot,
            shortItemCounts);
        }
      } else {
        for (MergedItemStack compactor : this.items) {
          for (int slot = 0, size = compactor.size(); slot < size; ++slot) {
            this.drawItem(matrices, compactor.getSubStack(slot), x, y, textRenderer, itemRenderer, slot,
              false);
          }
        }
      }
    } finally {
      
    }
  }
}
