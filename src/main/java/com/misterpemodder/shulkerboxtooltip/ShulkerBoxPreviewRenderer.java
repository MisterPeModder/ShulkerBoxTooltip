package com.misterpemodder.shulkerboxtooltip;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.misterpemodder.shulkerboxtooltip.mixin.ShulkerBoxSlotsAccessor;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ShulkerBoxPreviewRenderer {
  private static final Identifier TEXTURE =
      new Identifier("shulkerboxtooltip", "textures/gui/shulker_box_tooltip.png");
  public static final int TEXTURE_WIDTH = 176;
  public static final int TEXTURE_HEIGHT = 68;

  protected MinecraftClient client;
  protected TextRenderer textRenderer;
  protected ItemRenderer itemRenderer;
  protected final List<ItemStackCompactor> items;
  private ItemStack shulkerStack;
  protected ShulkerBoxPreviewType previewType;

  public ShulkerBoxPreviewRenderer() {
    this.client = MinecraftClient.getInstance();
    this.textRenderer = client.textRenderer;
    this.itemRenderer = client.getItemRenderer();
    this.items = new ArrayList<>();
    this.previewType = ShulkerBoxPreviewType.FULL;
    setShulkerStack(new ItemStack(Item.getItemFromBlock(Blocks.SHULKER_BOX)));
  }

  /**
   * Changes the shulker box stack to draw to preview for.
   * 
   * @param stack The stack, MUST be a kind a shulker box.
   */
  public void setShulkerStack(ItemStack stack) {
    CompoundTag compound = stack.getSubCompoundTag("BlockEntityTag");
    if (compound == null) {
      this.items.clear();
    } else if (!ItemStack.areEqual(this.shulkerStack, stack)) {
      this.items.clear();
      deserializeItems(compound);
    }
    this.shulkerStack = stack;
  }

  public void setPreviewType(ShulkerBoxPreviewType type) {
    this.previewType = type;
  }

  public int getWidth() {
    if (this.previewType == ShulkerBoxPreviewType.COMPACT)
      return 14 + Math.min(9, this.items.size()) * 18;
    return TEXTURE_WIDTH;
  }

  public int getHeight() {
    if (this.previewType == ShulkerBoxPreviewType.COMPACT)
      return 14 + (int) Math.ceil(this.items.size() / 9.0) * 18;
    return TEXTURE_HEIGHT;
  }

  protected void deserializeItems(CompoundTag compound) {
    if (!compound.containsKey("Items", NbtType.LIST))
      return;
    Map<Item, ItemStackCompactor> compactors = new HashMap<>();

    ListTag itemList = compound.getList("Items", 10);
    for (int i = 0; i < itemList.size(); ++i) {
      CompoundTag itemTag = itemList.getCompoundTag(i);
      ItemStack stack = ItemStack.fromTag(itemTag);
      ItemStackCompactor compactor = compactors.get(stack.getItem());
      if (compactor == null) {
        compactor = new ItemStackCompactor(ShulkerBoxSlotsAccessor.getAvailableSlots().length);
        compactors.put(stack.getItem(), compactor);
      }
      compactor.add(stack, itemTag.getByte("Slot"));
    }

    this.items.addAll(compactors.values());
    this.items.sort(Comparator.reverseOrder());
  }

  /**
   * Same as {@link Drawable#drawTexturedRect}, but accepts a zOffset as an argument.
   */
  public void drawTexturedRectZOffset(int x, int y, int u, int v, int w, int h, double zOffset) {
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
    DyeColor color =
        ((ShulkerBoxBlock) Block.getBlockFromItem(this.shulkerStack.getItem())).getColor();
    if (color != null) {
      float[] components = color.getColorComponents();
      GlStateManager.color4f(Math.max(0.15f, components[0]), Math.max(0.15f, components[1]),
          Math.max(0.15f, components[2]), 1.0f);
    } else {
      GlStateManager.color4f(0.592f, 0.403f, 0.592f, 1.0f);
    }

    this.client.getTextureManager().bindTexture(TEXTURE);
    GuiLighting.disable();
    final double zOffset = 800.0;
    if (this.previewType == ShulkerBoxPreviewType.COMPACT) {
      int size = Math.max(1, this.items.size());
      drawTexturedRectZOffset(x, y, 0, 0, 7, 7, zOffset);
      int a = Math.min(9, size) * 18;
      drawTexturedRectZOffset(x + 7, y, 7, 0, a, 7, zOffset);
      drawTexturedRectZOffset(x + 7 + a, y, 169, 0, 7, 7, zOffset);
      int b = (int) Math.ceil(size / 9.0) * 18;
      drawTexturedRectZOffset(x + 7 + a, y + 7, 169, 7, 7, b, zOffset);
      drawTexturedRectZOffset(x + 7 + a, y + 7 + b, 169, 62, 7, 7, zOffset);
      drawTexturedRectZOffset(x + 7, y + 7 + b, 7, 62, a, 7, zOffset);
      drawTexturedRectZOffset(x, y + 7 + b, 0, 62, 7, 7, zOffset);
      drawTexturedRectZOffset(x, y + 7, 0, 7, 7, b, zOffset);
      drawTexturedRectZOffset(x + 7, y + 7, 7, 7, a, b, zOffset);
    } else {
      drawTexturedRectZOffset(x, y, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, zOffset);
    }
    GuiLighting.enable();
  }

  public void draw(int x, int y) {
    if (this.items.isEmpty() || this.previewType == ShulkerBoxPreviewType.NO_PREVIEW)
      return;
    drawBackground(x, y);
    GuiLighting.enableForItems();
    this.itemRenderer.zOffset = 800.0f;
    if (this.previewType == ShulkerBoxPreviewType.COMPACT) {
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
        this.merged.addAmount(stack.getAmount());
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
      int ret = this.merged.getAmount() - other.merged.getAmount();
      if (ret != 0)
        return ret;
      return other.firstSlot - this.firstSlot;
    }
  }
}
