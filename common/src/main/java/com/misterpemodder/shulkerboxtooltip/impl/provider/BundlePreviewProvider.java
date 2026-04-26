package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BundlePreviewProvider implements PreviewProvider {
  @Override
  public boolean shouldDisplay(PreviewContext context) {
    var bundleContents = context.stack().get(DataComponents.BUNDLE_CONTENTS);
    return bundleContents != null && bundleContents.items().iterator().hasNext();
  }

  @Override
  @Environment(EnvType.CLIENT)
  public Configuration.Theme getTheme() {
    return ShulkerBoxTooltip.config.preview.themeBundle;
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return 64;
  }

  @Override
  public int getActiveSlotCount(PreviewContext context) {
    var bundleContents = context.stack().get(DataComponents.BUNDLE_CONTENTS);
    if (bundleContents == null) return 0;

    int usedWeight = 0;
    var weightOpt = bundleContents.weight().result();
    if (weightOpt.isPresent()) {
      var fraction = weightOpt.get();
      usedWeight = (fraction.getNumerator() * 64) / fraction.getDenominator();
    }

    int itemsCount = 0;
    for (var ignored : bundleContents.items()) {
      itemsCount++;
    }

    return itemsCount + Math.max(0, 64 - usedWeight);
  }

  @Override
  public List<ItemStack> getInventory(PreviewContext context) {
    int size = getInventoryMaxSize(context);
    var inv = NonNullList.withSize(size, ItemStack.EMPTY);

    var bundleContents = context.stack().get(DataComponents.BUNDLE_CONTENTS);
    if (bundleContents != null) {
      int activeSlots = getActiveSlotCount(context); // usable zone end
      int itemsCount = 0;
      for (var ignored : bundleContents.items()) {
        itemsCount++;
      }

      int i = Math.max(0, activeSlots - itemsCount); // push items to the bottom-right
      for (var template : bundleContents.items()) {
        if (i < activeSlots) {
          inv.set(i, template.create());
          i++;
        }
      }
    }

    return inv;
  }

  @Override
  @Environment(EnvType.CLIENT)
  public ColorKey getWindowColorKey(PreviewContext context) {
    Item item = context.stack().getItem();

    if (item == Items.WHITE_BUNDLE) return ColorKey.WHITE_BUNDLE;
    if (item == Items.ORANGE_BUNDLE) return ColorKey.ORANGE_BUNDLE;
    if (item == Items.MAGENTA_BUNDLE) return ColorKey.MAGENTA_BUNDLE;
    if (item == Items.LIGHT_BLUE_BUNDLE) return ColorKey.LIGHT_BLUE_BUNDLE;
    if (item == Items.YELLOW_BUNDLE) return ColorKey.YELLOW_BUNDLE;
    if (item == Items.LIME_BUNDLE) return ColorKey.LIME_BUNDLE;
    if (item == Items.PINK_BUNDLE) return ColorKey.PINK_BUNDLE;
    if (item == Items.GRAY_BUNDLE) return ColorKey.GRAY_BUNDLE;
    if (item == Items.LIGHT_GRAY_BUNDLE) return ColorKey.LIGHT_GRAY_BUNDLE;
    if (item == Items.CYAN_BUNDLE) return ColorKey.CYAN_BUNDLE;
    if (item == Items.PURPLE_BUNDLE) return ColorKey.PURPLE_BUNDLE;
    if (item == Items.BLUE_BUNDLE) return ColorKey.BLUE_BUNDLE;
    if (item == Items.BROWN_BUNDLE) return ColorKey.BROWN_BUNDLE;
    if (item == Items.GREEN_BUNDLE) return ColorKey.GREEN_BUNDLE;
    if (item == Items.RED_BUNDLE) return ColorKey.RED_BUNDLE;
    if (item == Items.BLACK_BUNDLE) return ColorKey.BLACK_BUNDLE;

    return ColorKey.BUNDLE;
  }

  @Override
  public boolean showTooltipHints(PreviewContext context) {
    return true;
  }

  @Override
  public int getMaxRowSize(PreviewContext context) {
    int global = context.config().defaultMaxRowSize();
    return (global == 9) ? 8 : global;
  }

  @Override
  public int getCompactMaxRowSize(PreviewContext context) {
    return this.getMaxRowSize(context);
  }
}
