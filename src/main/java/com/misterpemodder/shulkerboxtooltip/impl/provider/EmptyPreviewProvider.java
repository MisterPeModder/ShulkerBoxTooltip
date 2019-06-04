package com.misterpemodder.shulkerboxtooltip.impl.provider;

import java.util.Collections;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.api.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

public class EmptyPreviewProvider implements PreviewProvider {
  public static final PreviewProvider INSTANCE = new EmptyPreviewProvider();

  private EmptyPreviewProvider() {
  }

  @Override
  public String getModId() {
    return ShulkerBoxTooltip.MOD_ID;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Identifier> getPreviewItemsIds() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public DefaultedList<ItemStack> getInventory(ItemStack stack) {
    return null;
  }
}
