package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.config.PreviewConfiguration;
import com.misterpemodder.shulkerboxtooltip.api.provider.EmptyPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import com.misterpemodder.shulkerboxtooltip.impl.util.MergedItemStack;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class BasePreviewRenderer implements PreviewRenderer {

  protected PreviewType previewType;
  protected PreviewConfiguration config;
  protected int compactMaxRowSize;
  protected int maxRowSize;
  protected Identifier textureOverride;
  protected PreviewProvider provider;
  protected List<ItemStack> fullItems;
  protected List<MergedItemStack> compactItems;
  protected PreviewContext previewContext;

  protected final int slotWidth;
  protected final int slotHeight;
  protected final int slotXOffset;
  protected final int slotYOffset;

  protected BasePreviewRenderer(int slotWidth, int slotHeight, int slotXOffset, int slotYOffset) {
    this.fullItems = List.of();
    this.compactItems = List.of();
    this.previewType = PreviewType.FULL;
    this.maxRowSize = 9;

    this.slotWidth = slotWidth;
    this.slotHeight = slotHeight;
    this.slotXOffset = slotXOffset;
    this.slotYOffset = slotYOffset;

    var world = ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.level;

    this.setPreview(PreviewContext.builder(ItemStack.EMPTY)
        .withRegistryLookup(world == null ? null : world.registryAccess())
        .build(), EmptyPreviewProvider.INSTANCE);
  }

  protected int getMaxRowSize() {
    return this.previewType == PreviewType.COMPACT ? this.compactMaxRowSize : this.maxRowSize;
  }

  @Override
  public void setPreviewType(PreviewType type) {
    this.previewType = type;
  }

  @Override
  public void setPreview(PreviewContext context, PreviewProvider provider) {
    int rowSize = provider.getMaxRowSize(context);
    int compactRowSize = provider.getCompactMaxRowSize(context);

    this.config = context.config();
    if (compactRowSize <= 0)
      compactRowSize = this.config.defaultMaxRowSize();
    if (compactRowSize <= 0)
      compactRowSize = 9;
    if (rowSize <= 0)
      rowSize = compactRowSize;
    this.maxRowSize = rowSize;
    this.compactMaxRowSize = compactRowSize;
    this.textureOverride = provider.getTextureOverride(context);
    this.provider = provider;
    this.fullItems = provider.getInventory(context);
    this.compactItems = MergedItemStack.mergeInventory(this.fullItems, provider.getInventoryMaxSize(context),
        this.config.itemStackMergingStrategy());
    this.previewContext = context;
  }

  /**
   * Get the slot id at the given coordinates if X and Y are in bounds of the preview window
   *
   * @return The slot id at the given coordinates, or -1 if not found.
   */
  protected int getSlotAt(int x, int y) {
    int slot = -1;

    // Get the slot id at the given coordinates if X and Y are in bounds of the preview window
    if (x + 1 >= this.slotXOffset && y + 1 >= this.slotYOffset) {
      int maxRowSize = this.getMaxRowSize();
      int slotX = (x + 1 - this.slotXOffset) / this.slotWidth;
      int slotY = (y + 1 - this.slotYOffset) / this.slotHeight;

      if (slotX < maxRowSize)
        slot = slotX + slotY * maxRowSize;
    }

    return slot;
  }

  /**
   * @param x Top-left corner X coordinate of the preview window
   * @param y Top-left corner Y coordinate of the preview window
   * @return The item stack at the given coordinates, or {@link ItemStack#EMPTY} if not found.
   */
  private ItemStack getStackAt(int x, int y) {
    int slot = this.getSlotAt(x, y);

    if (this.previewType == PreviewType.COMPACT) {
      if (slot < 0 || slot >= this.compactItems.size())
        return ItemStack.EMPTY;
      MergedItemStack merged = this.compactItems.get(slot);

      return merged == null ? ItemStack.EMPTY : merged.get();
    } else if (slot >= 0 && slot < this.fullItems.size()) {
      return this.fullItems.get(slot);
    }
    return ItemStack.EMPTY;
  }

  protected void drawSlots(int x, int y, GuiGraphics graphics, Font font, int mouseX, int mouseY, int maxSlot) {
    int highlightedSlot = this.getSlotAt(mouseX - x, mouseY - y);

    if (this.previewType == PreviewType.COMPACT) {
      boolean shortItemCounts = this.config.shortItemCounts();

      for (int slot = 0, size = this.compactItems.size(); slot < size; ++slot) {
        if (slot <= maxSlot)
          this.drawSlot(this.compactItems.get(slot).get(), x, y, graphics, font, slot, highlightedSlot == slot,
              shortItemCounts);
      }
    } else {
      for (int slot = 0, size = this.fullItems.size(); slot < size; ++slot) {
        if (slot <= maxSlot)
          this.drawSlot(this.fullItems.get(slot), x, y, graphics, font, slot, highlightedSlot == slot, false);
      }
    }
  }

  protected abstract void drawSlot(ItemStack stack, int x, int y, GuiGraphics graphics, Font font, int slot,
      boolean isHighlighted, boolean shortItemCount);

  protected void drawItem(ItemStack stack, int x, int y, GuiGraphics graphics, Font font, boolean shortItemCount) {
    String countLabel = "";

    // stack size might exceed the maximum, so we create our own count label instead of the default
    if (stack.getCount() != 1) {
      if (shortItemCount)
        countLabel = ShulkerBoxTooltipUtil.abbreviateInteger(stack.getCount());
      else
        countLabel = String.valueOf(stack.getCount());
    }

    graphics.renderItem(stack, x, y);
    graphics.renderItemDecorations(font, stack, x, y, countLabel);
  }

  /**
   * Draw the tooltip that may be show when hovering a preview within a locked tooltip.
   */
  protected void drawInnerTooltip(int x, int y, GuiGraphics graphics, Font font, int mouseX, int mouseY) {
    ItemStack stack = this.getStackAt(mouseX - x, mouseY - y);

    if (stack.isEmpty())
      return;

    GuiGraphicsExtensions.renderTooltipImmediate(graphics,
        () -> graphics.setTooltipForNextFrame(font, stack, mouseX, mouseY));
  }
}
