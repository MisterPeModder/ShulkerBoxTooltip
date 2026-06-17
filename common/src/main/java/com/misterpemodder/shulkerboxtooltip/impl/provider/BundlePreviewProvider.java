package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.config.Theme;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ColorCollection;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BundlePreviewProvider implements PreviewProvider {
  @Environment(EnvType.CLIENT)
  private static Map<Item, ColorKey> DYED_BUNDLE_ITEM_TO_COLOR_KEY = null; // lazy-initialized

  @Override
  public boolean shouldDisplay(PreviewContext context) {
    var bundleContents = context.stack().get(DataComponents.BUNDLE_CONTENTS);
    return bundleContents != null && bundleContents.items().iterator().hasNext();
  }

  @Override
  @Environment(EnvType.CLIENT)
  public Theme getTheme() {
    return ShulkerBoxTooltip.config.preview.themeBundle;
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return 64;
  }

  @Override
  public int getActiveSlotCount(PreviewContext context) {
    var bundleContents = context.stack().get(DataComponents.BUNDLE_CONTENTS);
    if (bundleContents == null)
      return 0;

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
    if (DYED_BUNDLE_ITEM_TO_COLOR_KEY == null) {
      ColorCollection<ColorKey> colorKeys = new ColorCollection<>(ColorKey.WHITE_BUNDLE, ColorKey.ORANGE_BUNDLE,
          ColorKey.MAGENTA_BUNDLE, ColorKey.LIGHT_BLUE_BUNDLE, ColorKey.YELLOW_BUNDLE, ColorKey.LIME_BUNDLE,
          ColorKey.PINK_BUNDLE, ColorKey.GRAY_BUNDLE, ColorKey.LIGHT_GRAY_BUNDLE, ColorKey.CYAN_BUNDLE,
          ColorKey.PURPLE_BUNDLE, ColorKey.BLUE_BUNDLE, ColorKey.BROWN_BUNDLE, ColorKey.GREEN_BUNDLE,
          ColorKey.RED_BUNDLE, ColorKey.BLACK_BUNDLE);

      DYED_BUNDLE_ITEM_TO_COLOR_KEY = ColorCollection.zipMap(Items.DYED_BUNDLE, colorKeys, Pair::of)
          .asList()
          .stream()
          .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    Item item = context.stack().getItem();
    return DYED_BUNDLE_ITEM_TO_COLOR_KEY.getOrDefault(item, ColorKey.BUNDLE);
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
