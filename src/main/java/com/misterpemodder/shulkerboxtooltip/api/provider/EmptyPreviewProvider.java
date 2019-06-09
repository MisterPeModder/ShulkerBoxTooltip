package com.misterpemodder.shulkerboxtooltip.api.provider;

import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;

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
  public int getInventoryMaxSize(ItemStack stack) {
    return 0;
  }

  @Override
  public boolean shouldDisplay(ItemStack stack) {
    return false;
  }

  @Override
  public List<ItemStack> getInventory(ItemStack stack) {
    return Collections.emptyList();
  }
}
