package com.misterpemodder.shulkerboxtooltip.impl.hook;

import net.minecraft.nbt.ListTag;

public interface EnderChestInventoryPrevTagAccessor {
  ListTag shulkerboxtooltip$getPrevTags();

  void shulkerboxtooltip$setPrevTags(ListTag tags);
}
