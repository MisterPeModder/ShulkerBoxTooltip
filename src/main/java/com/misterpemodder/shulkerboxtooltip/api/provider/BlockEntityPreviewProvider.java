package com.misterpemodder.shulkerboxtooltip.api.provider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
 * as created by {@link Inventories#toTag(CompoundTag, DefaultedList)}.
 * </p>
 * @since 1.3.0
 */
public class BlockEntityPreviewProvider implements PreviewProvider {
  protected final int maxInvSize;
  protected final boolean canUseLootTables;

  /**
   * Creates a BlockEntityPreviewProvider instance.
   * @param maxInvSize             The maximum preview inventory size of the item
   *                               (may be lower than the actual inventory size)
   * @param canUseLootTables       If true, previews will not be shown when the {@code LootTable}
   *                               tag inside {@code BlockEntityData} is present.
   * @since 1.3.0
   */
  public BlockEntityPreviewProvider(int maxInvSize, boolean canUseLootTables) {
    this.maxInvSize = maxInvSize;
    this.canUseLootTables = canUseLootTables;
  }

  @Override
  public boolean shouldDisplay(ItemStack stack) {
    CompoundTag blockEntityTag = stack.getSubTag("BlockEntityTag");
    if (blockEntityTag == null || (this.canUseLootTables && blockEntityTag.contains("LootTable", 8))
        || !blockEntityTag.contains("Items", 9))
      return false;
    return !blockEntityTag.getList("Items", 10).isEmpty();
  }

  @Override
  public boolean showTooltipHints(ItemStack stack) {
    return stack.getSubTag("BlockEntityTag") != null;
  }

  @Override
  public List<ItemStack> getInventory(ItemStack stack) {
    List<ItemStack> inv = DefaultedList.ofSize(this.maxInvSize, ItemStack.EMPTY);
    CompoundTag blockEntityTag = stack.getSubTag("BlockEntityTag");

    if (blockEntityTag != null && blockEntityTag.contains("Items", 9)) {
      ListTag itemList = blockEntityTag.getList("Items", 10);

      if (itemList != null) {
        for (int i = 0, len = itemList.size(); i < len; ++i) {
          CompoundTag itemTag = itemList.getCompound(i);
          ItemStack s = ItemStack.fromTag(itemTag);
          byte slot;

          if (itemTag.contains("Slot", 1) && (slot = itemTag.getByte("Slot")) < this.maxInvSize)
            inv.set(slot, s);
        }
      }
    }
    return inv;
  }

  @Override
  public int getInventoryMaxSize(ItemStack stack) {
    return this.maxInvSize;
  }

  @Override
  public List<Text> addTooltip(ItemStack stack) {
    CompoundTag compound = stack.getTag();
    Style style = new Style().setColor(Formatting.GRAY);
    if (this.canUseLootTables && compound != null && compound.contains("BlockEntityTag", 10)) {
      CompoundTag blockEntityTag = compound.getCompound("BlockEntityTag");
      if (blockEntityTag != null && blockEntityTag.contains("LootTable", 8)) {
        switch (ShulkerBoxTooltipClient.config.main.lootTableInfoType) {
          case HIDE:
            return Collections.singletonList(new LiteralText("???????").setStyle(style));
          case SIMPLE:
            return Collections.singletonList(
                new TranslatableText("shulkerboxtooltip.hint.lootTable").setStyle(style));
          default:
            return Arrays.asList(
                new TranslatableText("shulkerboxtooltip.hint.lootTable.advanced").append(": "),
                new LiteralText(" " + blockEntityTag.getString("LootTable")).setStyle(style));
        }
      }
    }
    List<ItemStack> inventory = getInventory(stack);
    if (inventory != null) {
      int item_count = 0;
      for (ItemStack s : inventory) {
        if (s.getItem() != Items.AIR) {
          ++item_count;
        }
      }
      if (item_count > 0)
        return Collections.singletonList(
            new TranslatableText("container.shulkerbox.contains", item_count).setStyle(style));
    }
    return Collections
        .singletonList(new TranslatableText("container.shulkerbox.empty").setStyle(style));
  }
}
