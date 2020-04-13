package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;

public class FurnacePreviewProvider extends BlockEntityPreviewProvider {
  public FurnacePreviewProvider() {
    super(3, false);
  }

  @Override
  public boolean isFullPreviewAvailable(PreviewContext context) {
    return false;
  }
}
