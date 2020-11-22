package com.misterpemodder.shulkerboxtooltip.impl;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.provider.EmptyPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.CompactPreviewTagBehavior;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.Theme;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class DefaultPreviewRenderer implements PreviewRenderer {
  private static final Identifier DEFAULT_TEXTURE_LIGHT = new Identifier("shulkerboxtooltip",
      "textures/gui/shulker_box_tooltip.png");
  private static final Identifier DEFAULT_TEXTURE_DARK = new Identifier("shulkerboxtooltip",
      "textures/gui/shulker_box_tooltip_dark.png");
  public static final DefaultPreviewRenderer INSTANCE = new DefaultPreviewRenderer();

  private MinecraftClient client;
  private TextRenderer textRenderer;
  private ItemRenderer itemRenderer;
  private final List<ItemStackCompactor> items;
  private PreviewContext previewContext;
  private PreviewType previewType;
  private PreviewProvider provider;
  private int maxRowSize;
  private int compactMaxRowSize;
  private Identifier textureOverride;

  private DefaultPreviewRenderer() {
    this.client = MinecraftClient.getInstance();
    this.textRenderer = client.textRenderer;
    this.itemRenderer = client.getItemRenderer();
    this.items = new ArrayList<>();
    this.previewType = PreviewType.FULL;
    this.maxRowSize = 9;
    this.setPreview(PreviewContext.of(new ItemStack(Items.AIR)), EmptyPreviewProvider.INSTANCE);
  }

  @Override
  public void setPreview(PreviewContext context, PreviewProvider provider) {
    List<ItemStack> inventory = provider.getInventory(context);
    boolean ignoreData = ShulkerBoxTooltip.config.main.compactPreviewTagBehavior != CompactPreviewTagBehavior.SEPARATE;

    int rowSize = provider.getMaxRowSize(context);

    this.compactMaxRowSize = ShulkerBoxTooltip.config.main.defaultMaxRowSize;
    if (this.compactMaxRowSize <= 0)
      this.compactMaxRowSize = 9;
    if (rowSize <= 0)
      rowSize = this.compactMaxRowSize;
    this.maxRowSize = rowSize <= 0 ? 9 : rowSize;
    this.textureOverride = provider.getTextureOverride(context);

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
          compactor = new ItemStackCompactor(provider.getInventoryMaxSize(context));
          compactors.put(k, compactor);
        }
        compactor.add(s, i);
      }

      this.items.addAll(compactors.values());
      this.items.sort(Comparator.reverseOrder());
    }
    this.previewContext = context;
  }

  public int getMaxRowSize() {
    return this.previewType == PreviewType.COMPACT ? this.compactMaxRowSize : this.maxRowSize;
  }

  @Override
  public void setPreviewType(PreviewType type) {
    this.previewType = type;
  }

  @Override
  public int getWidth() {
    return 14 + Math.min(this.getMaxRowSize(), this.getInvSize()) * 18;
  }

  @Override
  public int getHeight() {
    return 14 + (int) Math.ceil(this.getInvSize() / (double) this.getMaxRowSize()) * 18;
  }

  /*
   * Same as DrawableHelper#blit, but accepts a zOffset as an argument.
   */
  public void blitZOffset(BufferBuilder builder, int x, int y, int u, int v, int w, int h, double zOffset) {
    builder.vertex(x, y + h, zOffset).texture(u * 0.00390625f, (v + h) * 0.00390625f).next();
    builder.vertex(x + w, y + h, zOffset).texture((u + w) * 0.00390625f, (v + h) * 0.00390625f).next();
    builder.vertex(x + w, y, zOffset).texture((u + w) * 0.00390625f, (v + 0) * 0.00390625f).next();
    builder.vertex(x, y, zOffset).texture(u * 0.00390625f, v * 0.00390625f).next();
  }

  private int getInvSize() {
    return this.previewType == PreviewType.COMPACT ? Math.max(1, this.items.size())
        : this.provider.getInventoryMaxSize(this.previewContext);
  }

  /**
   * <p>
   * Sets the color of the preview window.
   * </p>
   * <p>
   * The annotation is to suppress the Mojang Deprecation™ for
   * {@link RenderSystem#color3f(float, float, float)}.
   * <p>
   * @return the color that was used.
   */
  @SuppressWarnings("deprecation")
  private float[] setColor() {
    float[] color;

    if (ShulkerBoxTooltip.config.main.coloredPreview) {
      color = this.provider.getWindowColor(this.previewContext);
      if (color == null || color.length < 3) {
        color = PreviewProvider.DEFAULT_COLOR;
      }
    } else {
      color = PreviewProvider.DEFAULT_COLOR;
    }
    RenderSystem.color3f(color[0], color[1], color[2]);
    return color;
  }

  /**
   * Sets the texture to be used.
   * 
   * @param color An array of three floats.
   */
  private void setTexture(float[] color) {
    Identifier texture = this.textureOverride;

    if (texture == null) {
      Theme theme = ShulkerBoxTooltip.config.main.theme;

      if (theme == Theme.AUTO)
        theme = ShulkerBoxTooltipClient.isDarkModeEnabled() ? Theme.DARK : Theme.LIGHT;
      if (theme == Theme.DARK && (Arrays.equals(color, PreviewProvider.DEFAULT_COLOR)
          || Arrays.equals(color, DyeColor.WHITE.getColorComponents()))) {
        texture = DEFAULT_TEXTURE_DARK;
      } else {
        texture = DEFAULT_TEXTURE_LIGHT;
      }
    }
    this.client.getTextureManager().bindTexture(texture);
  }

  /**
   * <p>
   * Draws the preview window background
   * </p>
   * <p>
   * The annotation is to suppress the Mojang Deprecation™ for
   * {@link RenderSystem#enableAlphaTest()}.
   * <p>
   */
  @SuppressWarnings("deprecation")
  private void drawBackground(int x, int y) {
    this.setTexture(this.setColor());
    DiffuseLighting.disable();

    BufferBuilder builder = Tessellator.getInstance().getBuffer();

    builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

    final double zOffset = 800.0;
    int invSize = this.getInvSize();
    int xOffset = 7;
    int yOffset = 7;
    int rowSize = Math.min(this.getMaxRowSize(), invSize);
    int rowWidth = rowSize * 18;

    blitZOffset(builder, x, y, 0, 0, 7, 7, zOffset);
    for (int size = rowSize; size > 0; size -= 9) {
      int s = Math.min(size, 9);

      blitZOffset(builder, x + xOffset, y, 7, 0, s * 18, 7, zOffset);
      xOffset += s * 18;
    }
    blitZOffset(builder, x + rowWidth + 7, y, 169, 0, 7, 7, zOffset);

    int rowTexYPos = 7;

    while (invSize > 0) {
      xOffset = 7;
      blitZOffset(builder, x, y + yOffset, 0, rowTexYPos, 7, 18, zOffset);
      for (int rSize = rowSize; rSize > 0; rSize -= 9) {
        int s = Math.min(rSize, 9);

        blitZOffset(builder, x + xOffset, y + yOffset, 7, rowTexYPos, s * 18, 18, zOffset);
        xOffset += s * 18;
      }
      blitZOffset(builder, x + xOffset, y + yOffset, 169, rowTexYPos, 7, 18, zOffset);
      yOffset += 18;
      invSize -= rowSize;
      rowTexYPos = rowTexYPos >= 43 ? 7 : rowTexYPos + 18;
    }

    xOffset = 7;
    blitZOffset(builder, x, y + yOffset, 0, 61, 7, 7, zOffset);
    for (int size = rowSize; size > 0; size -= 9) {
      int s = Math.min(size, 9);

      blitZOffset(builder, x + xOffset, y + yOffset, 7, 61, s * 18, 7, zOffset);
      xOffset += s * 18;
    }
    blitZOffset(builder, x + rowWidth + 7, y + yOffset, 169, 61, 7, 7, zOffset);

    builder.end();
    RenderSystem.enableAlphaTest();
    BufferRenderer.draw(builder);
    DiffuseLighting.enable();
  }

  @Override
  public void draw(int x, int y) {
    if (this.items.isEmpty() || this.previewType == PreviewType.NO_PREVIEW)
      return;
    drawBackground(x, y);

    int maxRowSize = this.getMaxRowSize();

    this.itemRenderer.zOffset = 700.0f;
    if (this.previewType == PreviewType.COMPACT) {
      for (int i = 0, s = this.items.size(); i < s; ++i) {
        ItemStackCompactor compactor = this.items.get(i);
        int xOffset = 8 + x + 18 * (i % maxRowSize);
        int yOffset = 8 + y + 18 * (i / maxRowSize);
        ItemStack stack = compactor.getMerged();

        this.itemRenderer.renderInGuiWithOverrides(stack, xOffset, yOffset);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, xOffset, yOffset);
      }
    } else {
      for (ItemStackCompactor compactor : this.items) {
        for (int i = 0, size = compactor.size(); i < size; ++i) {
          int xOffset = 8 + x + 18 * (i % maxRowSize);
          int yOffset = 8 + y + 18 * (i / maxRowSize);
          ItemStack stack = compactor.get(i);

          this.itemRenderer.renderInGuiWithOverrides(stack, xOffset, yOffset);
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

      return key.item == this.item && key.id == this.id && (this.ignoreData || Objects.equals(key.data, this.data));
    }
  }
}
