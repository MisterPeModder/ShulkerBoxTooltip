package com.misterpemodder.shulkerboxtooltip.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.provider.EmptyPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.CompactPreviewTagBehavior;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class DefaultPreviewRenderer implements PreviewRenderer {
  private static final Identifier TEXTURE =
      new Identifier("shulkerboxtooltip", "textures/gui/shulker_box_tooltip.png");
  public static final DefaultPreviewRenderer INSTANCE = new DefaultPreviewRenderer();

  private MinecraftClient client;
  private TextRenderer textRenderer;
  private ItemRenderer itemRenderer;
  private final List<ItemStackCompactor> items;
  private ItemStack previewStack;
  private PreviewType previewType;
  private PreviewProvider provider;

  private DefaultPreviewRenderer() {
    this.client = MinecraftClient.getInstance();
    this.textRenderer = client.textRenderer;
    this.itemRenderer = client.getItemRenderer();
    this.items = new ArrayList<>();
    this.previewType = PreviewType.FULL;
    setPreview(new ItemStack(Items.AIR), EmptyPreviewProvider.INSTANCE);
  }

  @Override
  public void setPreview(ItemStack stack, PreviewProvider provider) {
    List<ItemStack> inventory = provider.getInventory(stack);
    boolean ignoreData =
        ShulkerBoxTooltip.config.main.compactPreviewTagBehavior != CompactPreviewTagBehavior.SEPARATE;
    this.provider = provider;
    this.items.clear();
    if (!inventory.isEmpty()) {
      Map<ItemKey, ItemStackCompactor> compactors = new HashMap<>();
      for (int i = 0, len = inventory.size(); i < len; ++i) {
        ItemStack s = inventory.get(i);
        if (s == ItemStack.EMPTY)
          continue;
        ItemKey k = new ItemKey(s, ignoreData);
        ItemStackCompactor compactor = compactors.get(k);
        if (compactor == null) {
          compactor = new ItemStackCompactor(provider.getInventoryMaxSize(stack));
          compactors.put(k, compactor);
        }
        compactor.add(s, i);
      }

      this.items.addAll(compactors.values());
      this.items.sort(Comparator.reverseOrder());
    }
    this.previewStack = stack.copy();
  }

  @Override
  public void setPreviewType(PreviewType type) {
    this.previewType = type;
  }

  @Override
  public int getWidth() {
    return 14 + Math.min(9, getInvSize()) * 18;
  }

  @Override
  public int getHeight() {
    return 14 + (int) Math.ceil(getInvSize() / 9.0) * 18;
  }

  /*
   * Same as DrawableHelper#blit, but accepts a zOffset as an argument.
   */
  public void blitZOffset(int x, int y, int u, int v, int w, int h, double zOffset) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBuffer();
    builder.begin(7, VertexFormats.POSITION_TEXTURE);
    builder.vertex(x, y + h, zOffset).texture(u * 0.00390625f, (v + h) * 0.00390625f).next();
    builder.vertex(x + w, y + h, zOffset).texture((u + w) * 0.00390625f, (v + h) * 0.00390625f)
        .next();
    builder.vertex(x + w, y, zOffset).texture((u + w) * 0.00390625f, (v + 0) * 0.00390625f).next();
    builder.vertex(x, y, zOffset).texture(u * 0.00390625f, v * 0.00390625f).next();
    tessellator.draw();
  }

  private int getInvSize() {
    return this.previewType == PreviewType.COMPACT ? Math.max(1, this.items.size())
        : this.provider.getInventoryMaxSize(this.previewStack);
  }

  private void drawBackground(int x, int y) {
    float[] color;
    if (ShulkerBoxTooltip.config.main.coloredPreview) {
      color = this.provider.getWindowColor(this.previewStack);
      if (color == null || color.length < 3) {
        color = PreviewProvider.DEFAULT_COLOR;
      }
    } else {
      color = PreviewProvider.DEFAULT_COLOR;
    }
    GlStateManager.color4f(color[0], color[1], color[2], 1.0f);

    this.client.getTextureManager().bindTexture(TEXTURE);
    GuiLighting.disable();
    final double zOffset = 800.0;
    int size = getInvSize();
    if (size <= 9) {
      blitZOffset(x, y, 0, 0, 7, 32, zOffset);
      int a = 18 * size;
      blitZOffset(x + 7, y, 7, 0, a, 32, zOffset);
      blitZOffset(x + 7 + a, y, 169, 0, 7, 32, zOffset);
    } else {
      int a = 7;
      blitZOffset(x, y, 0, 0, 175, 7, zOffset);
      while (size > 0) {
        blitZOffset(x, y + a, 0, 7, 175, 18, zOffset);
        a += 18;
        size -= 9;
      }
      blitZOffset(x, y + a, 0, 25, 175, 7, zOffset);
    }
    GuiLighting.enable();
  }

  @Override
  public void draw(int x, int y) {
    if (this.items.isEmpty() || this.previewType == PreviewType.NO_PREVIEW)
      return;
    drawBackground(x, y);
    this.itemRenderer.zOffset = 700.0f;
    if (this.previewType == PreviewType.COMPACT) {
      for (int i = 0, s = this.items.size(); i < s; ++i) {
        ItemStackCompactor compactor = this.items.get(i);
        int xOffset = 8 + x + 18 * (i % 9);
        int yOffset = 8 + y + 18 * (i / 9);
        ItemStack stack = compactor.getMerged();
        this.itemRenderer.renderGuiItem(this.client.player, stack, xOffset, yOffset);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, xOffset, yOffset);
      }
    } else {
      for (ItemStackCompactor compactor : this.items) {
        for (int i = 0, size = compactor.size(); i < size; ++i) {
          int xOffset = 8 + x + 18 * (i % 9);
          int yOffset = 8 + y + 18 * (i / 9);
          ItemStack stack = compactor.get(i);
          this.itemRenderer.renderGuiItem(this.client.player, stack, xOffset, yOffset);
          this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, xOffset, yOffset);
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
      this.subItems = DefaultedList.ofSize(slotCount, ItemStack.EMPTY);
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
        if (ShulkerBoxTooltip.config.main.compactPreviewTagBehavior == CompactPreviewTagBehavior.IGNORE)
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

  /**
   * Used has a key in maps
   */
  private class ItemKey {
    private final Item item;
    private final int id;
    private final CompoundTag data;
    private final boolean ignoreData;

    public ItemKey(ItemStack stack, boolean ignoreData) {
      this.item = stack.getItem();
      this.id = Registry.ITEM.getRawId(this.item);
      this.data = stack.getTag();
      this.ignoreData = ignoreData;
    }

    @Override
    public int hashCode() {
      return 31 * id + (this.ignoreData || data == null ? 0 : data.hashCode());
    }

    @Override
    public boolean equals(Object other) {
      if (this == other)
        return true;
      if (!(other instanceof ItemKey))
        return false;
      ItemKey key = (ItemKey) other;
      return key.item == this.item && key.id == this.id
          && (this.ignoreData || Objects.equals(key.data, this.data));
    }
  }
}
