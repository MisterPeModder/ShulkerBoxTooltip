package com.misterpemodder.shulkerboxtooltip.impl.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.EnderChestSyncType;
import com.misterpemodder.shulkerboxtooltip.impl.network.client.C2SPacketTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;

public class EnderChestPreviewProvider implements PreviewProvider {
  private static final float[] COLOR = new float[] {0.043f, 0.296f, 0.255f};

  @Override
  public List<ItemStack> getInventory(PreviewContext context) {
    PlayerEntity owner = context.getOwner();

    if (owner == null)
      return Collections.emptyList();

    EnderChestInventory inventory = owner.getEnderChestInventory();
    int size = inventory.getInvSize();
    List<ItemStack> items = DefaultedList.ofSize(size, ItemStack.EMPTY);

    for (int i = 0; i < size; ++i)
      items.set(i, inventory.getInvStack(i));
    return items;
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return context.getOwner().getEnderChestInventory().getInvSize();
  }

  @Override
  public boolean shouldDisplay(PreviewContext context) {
    return ShulkerBoxTooltip.config.server.clientIntegration
        && ShulkerBoxTooltip.config.server.enderChestSyncType != EnderChestSyncType.NONE
        && !context.getOwner().getEnderChestInventory().isInvEmpty();
  }

  @Override
  public float[] getWindowColor(PreviewContext context) {
    return COLOR;
  }

  @Override
  public void onInventoryAccessStart(PreviewContext context) {
    if (ShulkerBoxTooltip.config.server.enderChestSyncType == EnderChestSyncType.PASSIVE)
      C2SPacketTypes.ENDER_CHEST_UPDATE_REQUEST.sendToServer();
  }

  @Override
  public boolean showTooltipHints(PreviewContext context) {
    return ShulkerBoxTooltip.config.server.clientIntegration
        && ShulkerBoxTooltip.config.server.enderChestSyncType != EnderChestSyncType.NONE;
  }

  @Override
  public List<Text> addTooltip(PreviewContext context) {
    return BlockEntityPreviewProvider.getItemCountTooltip(new ArrayList<>(),
        this.getInventory(context));
  }
}

