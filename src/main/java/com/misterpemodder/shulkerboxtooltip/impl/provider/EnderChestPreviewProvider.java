package com.misterpemodder.shulkerboxtooltip.impl.provider;

import java.util.List;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.network.client.ClientConnectionHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class EnderChestPreviewProvider implements PreviewProvider {
  private MinecraftClient client;

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
    return ClientConnectionHandler.isServerAvailable();
  }

  @Override
  public float[] getWindowColor(ItemStack stack) {
    //return new float[] {0.156f, 0.218f, 0.226f};
    return new float[] {0.043f, 0.296f, 0.255f};
  }
}

