package com.misterpemodder.shulkerboxtooltip.api.provider;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;

/**
 * <p>
 * A PreviewProvider that works on items that carries block entity data.
 * </p>
 * <p>
 * Use/extend this when the target item(s) has the {@code Inventory} inside {@code BlockEntityData}
 * as created by {@link Inventories#toTag(CompoundTag, DefaultedList)}.
 * </p>
 */
public class BlockEntityPreviewProvider implements PreviewProvider {
  protected final int maxInvSize;
  protected final boolean hideIfLootTablePresent;

  /**
   * Creates a BlockEntityPreviewProvider instance.
   * @param maxInvSize             The maximum preview inventory size of the item
   *                               (may be lower than the actual inventory size)
   * @param hideIfLootTablePresent If true, previews will not be shown when the {@code LootTable}
   *                               tag inside {@code BlockEntityData} is present.
   */
  public BlockEntityPreviewProvider(int maxInvSize, boolean hideIfLootTablePresent) {
    this.maxInvSize = maxInvSize;
    this.hideIfLootTablePresent = hideIfLootTablePresent;
  }

  @Override
  public boolean shouldDisplay(ItemStack stack) {
    CompoundTag blockEntityTag = stack.getSubTag("BlockEntityTag");
    if (blockEntityTag == null
        || (this.hideIfLootTablePresent && blockEntityTag.containsKey("LootTable", 8))
        || !blockEntityTag.containsKey("Items", 9))
      return false;
    return !blockEntityTag.getList("Items", 10).isEmpty();
  }

  @Override
  public List<ItemStack> getInventory(ItemStack stack) {
    List<ItemStack> list = new ArrayList<>();
    CompoundTag blockEntityTag = stack.getSubTag("BlockEntityTag");
    if (blockEntityTag != null && blockEntityTag.containsKey("Items", 9)) {
      ListTag itemList = blockEntityTag.getList("Items", 10);
      for (int i = 0; i < itemList.size(); ++i) {
        list.add(ItemStack.fromTag(itemList.getCompoundTag(i)));
      }
    }
    return list;
  }

  @Override
  public int getInventoryMaxSize(ItemStack stack) {
    return this.maxInvSize;
  }
}
