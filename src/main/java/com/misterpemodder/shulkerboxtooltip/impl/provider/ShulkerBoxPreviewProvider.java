package com.misterpemodder.shulkerboxtooltip.impl.provider;

import java.util.ArrayList;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.api.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class ShulkerBoxPreviewProvider implements PreviewProvider {

  @Override
  public String getModId() {
    return ShulkerBoxTooltip.MOD_ID;
  }

  @Override
  public List<Identifier> getPreviewItemsIds() {
    List<Identifier> ids = new ArrayList<>();
    ids.add(new Identifier("minecraft", "shulker_box"));
    for (DyeColor color : DyeColor.values()) {
      ids.add(new Identifier("minecraft", color.getName() + "_shulker_box"));
    }
    return ids;
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
