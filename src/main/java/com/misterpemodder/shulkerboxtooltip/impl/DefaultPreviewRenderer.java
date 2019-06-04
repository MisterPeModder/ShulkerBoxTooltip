package com.misterpemodder.shulkerboxtooltip.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.misterpemodder.shulkerboxtooltip.api.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.mixin.ShulkerBoxSlotsAccessor;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DefaultPreviewRenderer implements PreviewRenderer {
  private static final Identifier TEXTURE =
      new Identifier("shulkerboxtooltip", "textures/gui/shulker_box_tooltip.png");
  public static final int TEXTURE_WIDTH = 176;
  public static final int TEXTURE_HEIGHT = 68;

  protected MinecraftClient client;
  protected TextRenderer textRenderer;
  protected ItemRenderer itemRenderer;
  protected final List<ItemStackCompactor> items;
  private ItemStack previewStack;
  protected PreviewType previewType;
  private PreviewProvider provider;

  public DefaultPreviewRenderer() {
    this.client = MinecraftClient.getInstance();
    this.textRenderer = client.textRenderer;
    this.itemRenderer = client.getItemRenderer();
    this.items = new ArrayList<>();
    this.previewType = PreviewType.FULL;
    setPreview(new ItemStack(Item.fromBlock(Blocks.SHULKER_BOX)), PreviewProvider.EMPTY);
  }

  @Override
  public void setPreview(ItemStack stack, PreviewProvider provider) {
    this.provider = provider;
    DefaultedList<ItemStack> inventory = provider.getInventory(stack);
    if (inventory == null || inventory.isEmpty()) {
      this.items.clear();
    } else if (!ItemStack.areItemsEqual(this.previewStack, stack)) {
      this.items.clear();
      Map<Item, ItemStackCompactor> compactors = new HashMap<>();
      for (int i = 0, len = inventory.size(); i < len; ++i) {
        ItemStack s = inventory.get(i);
        ItemStackCompactor compactor = compactors.get(s.getItem());
        if (compactor == null) {
          compactor = new ItemStackCompactor(ShulkerBoxSlotsAccessor.getAvailableSlots().length);
          compactors.put(s.getItem(), compactor);
        }
        compactor.add(s, i);
      }

      this.items.addAll(compactors.values());
      this.items.sort(Comparator.reverseOrder());
    }
    this.previewStack = stack;
  }

  @Override
  public void setPreviewType(PreviewType type) {
    this.previewType = type;
  }

  @Override
  public int getWidth() {
    if (this.previewType == PreviewType.COMPACT)
      return 14 + Math.min(9, this.items.size()) * 18;
    return TEXTURE_WIDTH;
  }

  @Override
  public int getHeight() {
    if (this.previewType == PreviewType.COMPACT)
      return 14 + (int) Math.ceil(this.items.size() / 9.0) * 18;
    return TEXTURE_HEIGHT;
  }

  /*
   * Same as DrawableHelper#blit, but accepts a zOffset as an argument.
   */
  public void blitZOffset(int x, int y, int u, int v, int w, int h, double zOffset) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBufferBuilder();
    builder.begin(7, VertexFormats.POSITION_UV);
    builder.vertex(x, y + h, zOffset).texture(u * 0.00390625, (v + h) * 0.00390625).next();
    builder.vertex(x + w, y + h, zOffset).texture((u + w) * 0.00390625, (v + h) * 0.00390625)
        .next();
    builder.vertex(x + w, y, zOffset).texture((u + w) * 0.00390625, (v + 0) * 0.00390625).next();
    builder.vertex(x, y, zOffset).texture(u * 0.00390625, v * 0.00390625).next();
    tessellator.draw();
  }

  protected void drawBackground(int x, int y) {
    float[] color = this.provider.getWindowColor(this.previewStack);
    if (color == null || color.length < 3) {
      color = PreviewProvider.DEFAULT_COLOR;
    }
    GlStateManager.color4f(color[0], color[1], color[2], 1.0f);

    this.client.getTextureManager().bindTexture(TEXTURE);
    GuiLighting.disable();
    final double zOffset = 800.0;
    if (this.previewType == PreviewType.COMPACT) {
      int size = Math.max(1, this.items.size());
      blitZOffset(x, y, 0, 0, 7, 7, zOffset);
      int a = Math.min(9, size) * 18;
      blitZOffset(x + 7, y, 7, 0, a, 7, zOffset);
      blitZOffset(x + 7 + a, y, 169, 0, 7, 7, zOffset);
      int b = (int) Math.ceil(size / 9.0) * 18;
      blitZOffset(x + 7 + a, y + 7, 169, 7, 7, b, zOffset);
      blitZOffset(x + 7 + a, y + 7 + b, 169, 62, 7, 7, zOffset);
      blitZOffset(x + 7, y + 7 + b, 7, 62, a, 7, zOffset);
      blitZOffset(x, y + 7 + b, 0, 62, 7, 7, zOffset);
      blitZOffset(x, y + 7, 0, 7, 7, b, zOffset);
      blitZOffset(x + 7, y + 7, 7, 7, a, b, zOffset);
    } else {
      blitZOffset(x, y, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, zOffset);
    }
    GuiLighting.enable();
  }

  @Override
  public void draw(int x, int y) {
    if (this.items.isEmpty() || this.previewType == PreviewType.NO_PREVIEW)
      return;
    drawBackground(x, y);
    GuiLighting.enableForItems();
    this.itemRenderer.zOffset = 800.0f;
    if (this.previewType == PreviewType.COMPACT) {
      for (int i = 0, s = this.items.size(); i < s; ++i) {
        ItemStackCompactor compactor = this.items.get(i);
        int xOffset = 8 + x + 18 * (i % 9);
        int yOffset = 8 + y + 18 * (i / 9);
        this.itemRenderer.renderGuiItem(this.client.player, compactor.getMerged(), xOffset,
            yOffset);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, compactor.getMerged(), xOffset,
            yOffset);
      }
    } else {
      for (ItemStackCompactor compactor : this.items) {
        for (int index = 0, size = compactor.size(); index < size; ++index) {
          int xOffset = 8 + x + 18 * (index % 9);
          int yOffset = 8 + y + 18 * (index / 9);
          this.itemRenderer.renderGuiItem(this.client.player, compactor.get(index), xOffset,
              yOffset);
          this.itemRenderer.renderGuiItemOverlay(this.textRenderer, compactor.get(index), xOffset,
              yOffset);
        }
      }
    }
    this.itemRenderer.zOffset = 0.0f;
  }

  protected static class ItemStackCompactor implements Comparable<ItemStackCompactor> {
    private ItemStack merged;
    private DefaultedList<ItemStack> subItems;
    private int firstSlot;

    public ItemStackCompactor(int slotCount) {
      this.merged = ItemStack.EMPTY;
      this.subItems = DefaultedList.create(slotCount, ItemStack.EMPTY);
      this.firstSlot = Integer.MAX_VALUE;
    }

    public ItemStack getMerged() {
      return this.merged;
    }

    /**
     * Add the passed stack into the item list. Does not check if items are equal.
     * 
     * @param stack The stack to add
     * @param slot  The slot this stack is located in.
     */
    public void add(ItemStack stack, int slot) {
      if (slot < 0 || slot >= this.subItems.size())
        return;
      this.subItems.set(slot, stack);
      if (slot < this.firstSlot)
        this.firstSlot = slot;
      if (this.merged == ItemStack.EMPTY) {
        this.merged = stack.copy();
        this.merged.setTag(null);
      } else {
        this.merged.increment(stack.getCount());
      }
    }

    public ItemStack get(int slot) {
      return this.subItems.get(slot);
    }

    public int size() {
      return this.subItems.size();
    }

    @Override
    public int compareTo(ItemStackCompactor other) {
      int ret = this.merged.getCount() - other.merged.getCount();
      if (ret != 0)
        return ret;
      return other.firstSlot - this.firstSlot;
    }
  }
}
