package com.misterpemodder.shulkerboxtooltip.impl.util;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.CompactPreviewNbtBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MergedItemStack implements Comparable<MergedItemStack> {
  private ItemStack merged;
  private final DefaultedList<ItemStack> subItems;
  private int firstSlot;

  public MergedItemStack(int slotCount) {
    this.merged = ItemStack.EMPTY;
    this.subItems = DefaultedList.ofSize(slotCount, ItemStack.EMPTY);
    this.firstSlot = Integer.MAX_VALUE;
  }

  public ItemStack get() {
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
    this.subItems.set(slot, stack.copy());
    if (slot < this.firstSlot)
      this.firstSlot = slot;
    if (this.merged.isEmpty()) {
      this.merged = stack.copy();
      if (ShulkerBoxTooltip.config.preview.compactPreviewNbtBehavior == CompactPreviewNbtBehavior.IGNORE)
        this.merged.setNbt(null);
    } else {
      this.merged.increment(stack.getCount());
    }
  }

  public ItemStack getSubStack(int slot) {
    return this.subItems.get(slot);
  }

  public int size() {
    return this.subItems.size();
  }

  @Override
  public int compareTo(MergedItemStack other) {
    int ret = this.merged.getCount() - other.merged.getCount();

    if (ret != 0)
      return ret;
    return other.firstSlot - this.firstSlot;
  }

  public static List<MergedItemStack> mergeInventory(List<ItemStack> inventory, int maxSize,
      boolean ignoreData) {
    var items = new ArrayList<MergedItemStack>();

    if (!inventory.isEmpty()) {
      var mergedStacks = new HashMap<ItemKey, MergedItemStack>();

      for (int i = 0, len = inventory.size(); i < len; ++i) {
        ItemStack s = inventory.get(i);

        if (s.isEmpty())
          continue;

        ItemKey k = new ItemKey(s, ignoreData);
        MergedItemStack mergedStack = mergedStacks.get(k);

        if (mergedStack == null) {
          mergedStack = new MergedItemStack(maxSize);
          mergedStacks.put(k, mergedStack);
        }
        mergedStack.add(s, i);
      }

      items.addAll(mergedStacks.values());
      items.sort(Comparator.reverseOrder());
    }
    return items;
  }
}
