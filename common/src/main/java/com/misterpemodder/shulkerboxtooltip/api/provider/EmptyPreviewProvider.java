package com.misterpemodder.shulkerboxtooltip.api.provider;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * A PreviewProvider that does nothing.
 * @since 1.3.0
 */
public class EmptyPreviewProvider implements PreviewProvider {
  /**
   * The EmptyPreviewProvider instance.
   * @since 1.3.0
   */
  public static final PreviewProvider INSTANCE = new EmptyPreviewProvider();

  protected EmptyPreviewProvider() {
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return 0;
  }

  @Override
  public boolean shouldDisplay(PreviewContext context) {
    return false;
  }

  @Override
  public List<ItemStack> getInventory(PreviewContext context) {
    return Collections.emptyList();
  }
}
