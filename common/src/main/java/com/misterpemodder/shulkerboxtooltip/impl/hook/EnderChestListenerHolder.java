package com.misterpemodder.shulkerboxtooltip.impl.hook;

import com.misterpemodder.shulkerboxtooltip.impl.network.EnderChestListener;
import org.jetbrains.annotations.Nullable;

public interface EnderChestListenerHolder {
  @Nullable EnderChestListener shulkerBoxTooltip$getListener();

  void shulkerBoxTooltip$setListener(@Nullable EnderChestListener listener);
}
