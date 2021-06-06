package com.misterpemodder.shulkerboxtooltip.impl.util;

import java.util.Objects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.Registry;

/**
 * Used has a key in maps
 */
public class ItemKey {
  private final Item item;
  private final int id;
  private final NbtCompound data;
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
