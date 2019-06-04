package com.misterpemodder.shulkerboxtooltip.impl.provider;

import java.util.Collections;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.api.PreviewProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

public class EmptyPreviewProvider implements PreviewProvider {
  public static final PreviewProvider INSTANCE = new EmptyPreviewProvider();

  private EmptyPreviewProvider() {
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Item> getPreviewItems() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public DefaultedList<ItemStack> getInventory(ItemStack stack) {
    return null;
  }
}
