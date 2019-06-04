package com.misterpemodder.shulkerboxtooltip.impl.provider;

import java.util.Arrays;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.api.PreviewProvider;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;

public class ShulkerBoxPreviewProvider implements PreviewProvider {
  @Override
  public List<Item> getPreviewItems() {
    return Arrays.asList(Items.SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX,
        Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX,
        Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
        Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
        Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
        Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX);
  }

  @Override
  public DefaultedList<ItemStack> getInventory(ItemStack stack) {
    DefaultedList<ItemStack> list = DefaultedList.create();
    CompoundTag blockEntityTag = stack.getSubTag("BlockEntityTag");
    if (blockEntityTag != null && blockEntityTag.containsKey("Items", 9)) {
      ListTag itemList = blockEntityTag.getList("Items", 10);
      for (int i = 0; i < itemList.size(); ++i) {
        list.add(ItemStack.fromTag(itemList.getCompoundTag(i)));
      }
    }
    return list;
  }

  @Override
  public float[] getWindowColor(ItemStack stack) {
    DyeColor dye = ((ShulkerBoxBlock) Block.getBlockFromItem(stack.getItem())).getColor();
    if (dye != null) {
      float[] components = dye.getColorComponents();
      return new float[] {Math.max(0.15f, components[0]), Math.max(0.15f, components[1]),
          Math.max(0.15f, components[2])};
    } else {
      return PreviewProvider.DEFAULT_COLOR;
    }
  }
}
