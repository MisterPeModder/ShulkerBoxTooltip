package com.misterpemodder.shulkerboxtooltip.impl.provider;

import java.util.List;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.EnderChestSyncType;
import com.misterpemodder.shulkerboxtooltip.impl.network.client.C2SPacketTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class EnderChestPreviewProvider implements PreviewProvider {
  private MinecraftClient client;
  private static final float[] COLOR = new float[] {0.043f, 0.296f, 0.255f};

  public EnderChestPreviewProvider() {
    this.client = MinecraftClient.getInstance();
  }

  @Override
  public List<ItemStack> getInventory(ItemStack stack) {
    EnderChestInventory inventory = this.client.player.getEnderChestInventory();
    int size = inventory.size();
    List<ItemStack> items = DefaultedList.ofSize(size, ItemStack.EMPTY);

    for (int i = 0; i < size; ++i)
      items.set(i, inventory.getStack(i));
    return items;
  }

  @Override
  public int getInventoryMaxSize(ItemStack stack) {
    return this.client.player.getEnderChestInventory().size();
  }

  @Override
  public boolean shouldDisplay(ItemStack stack) {
    return ShulkerBoxTooltip.config.server.clientIntegration
        && ShulkerBoxTooltip.config.server.enderChestSyncType != EnderChestSyncType.NONE;
  }

  @Override
  public float[] getWindowColor(ItemStack stack) {
    return COLOR;
  }

  @Override
  public void onOpenPreview(ItemStack stack) {
    if (ShulkerBoxTooltip.config.server.enderChestSyncType == EnderChestSyncType.PASSIVE)
      C2SPacketTypes.ENDER_CHEST_UPDATE_REQUEST.sendToServer();
  }
}

