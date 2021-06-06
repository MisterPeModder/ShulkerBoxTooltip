package com.misterpemodder.shulkerboxtooltip.impl.hook;

import net.minecraft.nbt.NbtList;

public interface EnderChestInventoryPrevTagAccessor {
  NbtList shulkerboxtooltip$getPrevTags();

  void shulkerboxtooltip$setPrevTags(NbtList tags);
}
