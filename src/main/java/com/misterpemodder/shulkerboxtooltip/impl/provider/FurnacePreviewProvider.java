package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import net.minecraft.item.ItemStack;

public class FurnacePreviewProvider extends BlockEntityPreviewProvider {
  public FurnacePreviewProvider() {
    super(3, false);
  }

  @Override
  public boolean isFullPreviewAvailable(ItemStack stack) {
    return false;
  }
}
