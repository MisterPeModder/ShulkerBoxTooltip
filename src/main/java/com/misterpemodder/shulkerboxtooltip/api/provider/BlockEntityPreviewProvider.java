package com.misterpemodder.shulkerboxtooltip.api.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

/**
 * <p>
 * A PreviewProvider that works on items that carries block entity data.
 * </p>
 * <p>
 * Use/extend this when the target item(s) has the {@code Inventory} inside {@code BlockEntityData}
 * as created by {@link Inventories#writeNbt(NbtCompound, DefaultedList)}.
 * </p>
 * @since 1.3.0
 */
public class BlockEntityPreviewProvider implements PreviewProvider {
  protected final int maxInvSize;
  protected final boolean canUseLootTables;
  protected final int maxRowSize;

  /**
   * Creates a BlockEntityPreviewProvider instance.
   * 
   * @param maxInvSize       The maximum preview inventory size of the item
   *                         (may be lower than the actual inventory size).
   *                         If the inventory size insn't constant,
   *                         overidde {@link #getInventoryMaxSize(PreviewContext)}
   *                         and use {@code maxInvSize} as a default value.
   * @param canUseLootTables If true, previews will not be shown when the {@code LootTable}
   *                         tag inside {@code BlockEntityData} is present.
   * @since 1.3.0
   */
  public BlockEntityPreviewProvider(int maxInvSize, boolean canUseLootTables) {
    this.maxInvSize = maxInvSize;
    this.canUseLootTables = canUseLootTables;
    this.maxRowSize = 9;
  }

  /**
   * Creates a BlockEntityPreviewProvider instance.
   * 
   * @param maxInvSize       The maximum preview inventory size of the item
   *                         (may be lower than the actual inventory size).
   *                         If the inventory size insn't constant,
   *                         overidde {@link #getInventoryMaxSize(PreviewContext)}
   *                         and use {@code maxInvSize} as a default value.
   * @param canUseLootTables If true, previews will not be shown when the {@code LootTable}
   *                         tag inside {@code BlockEntityData} is present.
   * @param maxRowSize       The maximum number of item stacks to be displayed in a row.
   *                         If less or equal to zero, defaults to 9.
   * @since 2.0.0
   */
  public BlockEntityPreviewProvider(int maxInvSize, boolean canUseLootTables, int maxRowSize) {
    this.maxInvSize = maxInvSize;
    this.canUseLootTables = canUseLootTables;
    this.maxRowSize = maxRowSize <= 0 ? 9 : maxRowSize;
  }

  @Override
  public boolean shouldDisplay(PreviewContext context) {
    NbtCompound blockEntityTag = context.getStack().getSubTag("BlockEntityTag");

    if (blockEntityTag == null || (this.canUseLootTables && blockEntityTag.contains("LootTable", 8))
        || !blockEntityTag.contains("Items", 9))
      return false;
    return !blockEntityTag.getList("Items", 10).isEmpty();
  }

  @Override
  public boolean showTooltipHints(PreviewContext context) {
    return context.getStack().getSubTag("BlockEntityTag") != null;
  }

  @Override
  public List<ItemStack> getInventory(PreviewContext context) {
    int invMaxSize = this.getInventoryMaxSize(context);
    List<ItemStack> inv = DefaultedList.ofSize(invMaxSize, ItemStack.EMPTY);
    NbtCompound blockEntityTag = context.getStack().getSubTag("BlockEntityTag");

    if (blockEntityTag != null && blockEntityTag.contains("Items", 9)) {
      NbtList itemList = blockEntityTag.getList("Items", 10);

      if (itemList != null) {
        for (int i = 0, len = itemList.size(); i < len; ++i) {
          NbtCompound itemTag = itemList.getCompound(i);
          ItemStack s = ItemStack.fromNbt(itemTag);
          byte slot;

          if (itemTag.contains("Slot", 1) && (slot = itemTag.getByte("Slot")) < invMaxSize)
            inv.set(slot, s);
        }
      }
    }
    return inv;
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return this.maxInvSize;
  }

  @Override
  public List<Text> addTooltip(PreviewContext context) {
    ItemStack stack = context.getStack();
    NbtCompound compound = stack.getTag();
    Style style = Style.EMPTY.withColor(Formatting.GRAY);

    if (this.canUseLootTables && compound != null && compound.contains("BlockEntityTag", 10)) {
      NbtCompound blockEntityTag = compound.getCompound("BlockEntityTag");

      if (blockEntityTag != null && blockEntityTag.contains("LootTable", 8)) {
        switch (ShulkerBoxTooltip.config.main.lootTableInfoType) {
          case HIDE:
            return Collections.singletonList(new LiteralText("???????").setStyle(style));
          case SIMPLE:
            return Collections.singletonList(
                new TranslatableText("shulkerboxtooltip.hint.lootTable").setStyle(style));
          default:
            return Arrays.asList(
                new TranslatableText("shulkerboxtooltip.hint.lootTable.advanced")
                    .append(new LiteralText(": ")),
                new LiteralText(" " + blockEntityTag.getString("LootTable")).setStyle(style));
        }
      }
    }
    return getItemListTooltip(new ArrayList<>(), this.getInventory(context), style);
  }

  /**
   * Adds the number of items to the passed tooltip, adds 'empty' if there is no items to count.
   * 
   * @param tooltip The tooltip in which to add the item count.
   * @param items   The list of items to display, may be null or empty.
   * @return The passed tooltip, to allow chaining.
   * @since 2.0.0
   */
  public static List<Text> getItemCountTooltip(List<Text> tooltip,
      @Nullable List<ItemStack> items) {
    return getItemListTooltip(tooltip, items, Style.EMPTY.withColor(Formatting.GRAY));
  }

  /**
   * Adds the number of items to the passed tooltip, adds 'empty' if there is no items to count.
   * 
   * @param tooltip The tooltip in which to add the item count.
   * @param items   The list of items to display, may be null or empty.
   * @param style   The formatting style of the tooltip.
   * @return The passed tooltip, to allow chaining.
   * @since 2.0.0
   */
  public static List<Text> getItemListTooltip(List<Text> tooltip, @Nullable List<ItemStack> items,
      Style style) {
    if (items != null) {
      int item_count = 0;

      for (ItemStack s : items) {
        if (s.getItem() != Items.AIR) {
          ++item_count;
        }
      }
      if (item_count > 0) {
        tooltip
            .add(new TranslatableText("container.shulkerbox.contains", item_count).setStyle(style));
        return tooltip;
      }
    }
    tooltip.add(new TranslatableText("container.shulkerbox.empty").setStyle(style));
    return tooltip;
  }

  @Override
  public int getMaxRowSize(PreviewContext context) {
    return this.maxRowSize;
  }
}
