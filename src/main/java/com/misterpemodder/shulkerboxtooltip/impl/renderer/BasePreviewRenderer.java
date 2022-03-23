package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import java.util.ArrayList;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.provider.EmptyPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.CompactPreviewNbtBehavior;
import com.misterpemodder.shulkerboxtooltip.impl.util.MergedItemStack;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public abstract class BasePreviewRenderer implements PreviewRenderer {
  protected PreviewType previewType;
  protected int compactMaxRowSize;
  protected int maxRowSize;
  protected Identifier textureOverride;
  protected PreviewProvider provider;
  protected List<MergedItemStack> items;
  protected PreviewContext previewContext;

  protected BasePreviewRenderer() {
    this.items = new ArrayList<>();
    this.previewType = PreviewType.FULL;
    this.maxRowSize = 9;
    this.setPreview(PreviewContext.of(new ItemStack(Items.AIR)), EmptyPreviewProvider.INSTANCE);
  }

  protected int getMaxRowSize() {
    return this.previewType == PreviewType.COMPACT ? this.compactMaxRowSize : this.maxRowSize;
  }

  protected int getInvSize() {
    return this.previewType == PreviewType.COMPACT ? Math.max(1, this.items.size())
        : this.provider.getInventoryMaxSize(this.previewContext);
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
        ShulkerBoxTooltip.config.preview.compactPreviewNbtBehavior != CompactPreviewNbtBehavior.SEPARATE);
    this.previewContext = context;
  }

  protected void renderMergedStack(ItemStack stack, ItemRenderer itemRenderer,
      TextRenderer textRenderer, int x, int y) {
    itemRenderer.renderInGuiWithOverrides(stack, x, y);
    if (!ShulkerBoxTooltip.config.preview.shortItemCounts || stack.getCount() == 1)
      itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y);
    else
      itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y,
          ShulkerBoxTooltipUtil.abrieviateInteger(stack.getCount()));
  }

  protected void renderSubStack(ItemStack stack, ItemRenderer itemRenderer,
      TextRenderer textRenderer, int x, int y, int slot) {
    itemRenderer.renderInGuiWithOverrides(stack, x, y, slot);
    itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y);
  }
}
