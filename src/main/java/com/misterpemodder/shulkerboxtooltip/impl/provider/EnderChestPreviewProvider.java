package com.misterpemodder.shulkerboxtooltip.impl.provider;

import java.util.Collections;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.EnderChestSyncType;
import com.misterpemodder.shulkerboxtooltip.impl.network.client.C2SPacketTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class EnderChestPreviewProvider implements PreviewProvider {
  private static final float[] COLOR = new float[] {0.043f, 0.296f, 0.255f};

  @Override
  public List<ItemStack> getInventory(PreviewContext context) {
    PlayerEntity owner = context.getOwner();

    if (owner == null)
      return Collections.emptyList();

    EnderChestInventory inventory = owner.getEnderChestInventory();
    int size = inventory.size();
    List<ItemStack> items = DefaultedList.ofSize(size, ItemStack.EMPTY);

    for (int i = 0; i < size; ++i)
      items.set(i, inventory.getStack(i));
    return items;
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return context.getOwner().getEnderChestInventory().size();
  }

  @Override
  public boolean shouldDisplay(PreviewContext context) {
    return ShulkerBoxTooltip.config.server.clientIntegration
        && ShulkerBoxTooltip.config.server.enderChestSyncType != EnderChestSyncType.NONE;
  }

  @Override
  public float[] getWindowColor(PreviewContext context) {
    return COLOR;
  }

  @Override
  public void onOpenPreview(PreviewContext context) {
    if (ShulkerBoxTooltip.config.server.enderChestSyncType == EnderChestSyncType.PASSIVE)
      C2SPacketTypes.ENDER_CHEST_UPDATE_REQUEST.sendToServer();
  }
}

