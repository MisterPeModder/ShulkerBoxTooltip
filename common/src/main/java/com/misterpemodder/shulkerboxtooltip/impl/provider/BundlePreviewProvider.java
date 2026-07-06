package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.config.Theme;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.config.BundleTheme;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
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
    if (ShulkerBoxTooltip.config.preview.themeBundle == BundleTheme.VANILLA) {
      return false;
    }
    var bundleContents = context.stack().get(DataComponents.BUNDLE_CONTENTS);
    return bundleContents != null && bundleContents.items().iterator().hasNext();
  }

  @Override
  @Environment(EnvType.CLIENT)
  public Theme getTheme() {
    return switch (ShulkerBoxTooltip.config.preview.themeBundle) {
      case SHULKERBOXTOOLTIP -> Theme.SHULKERBOXTOOLTIP;
      case VANILLA, VANILLA_PLUS -> Theme.VANILLA;
    };
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return 64;
  }

  @Override
  public int getActiveSlotCount(PreviewContext context) {
    var bundleContents = context.stack().getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
    return bundleContents.size();
  }

  @Override
  public int getSelectedSlot(PreviewContext context) {
    var bundleContents = context.stack().get(DataComponents.BUNDLE_CONTENTS);
    if (bundleContents == null)
      return -1;

    int selectedIndex = bundleContents.getSelectedItemIndex();
    if (selectedIndex == BundleContents.NO_SELECTED_ITEM_INDEX)
      return -1;

    int activeSlots = getActiveSlotCount(context);
    int itemsCount = 0;
    for (var ignored : bundleContents.items()) {
      itemsCount++;
    }
    int startSlot = Math.max(0, activeSlots - itemsCount);
    return startSlot + selectedIndex;
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
  public String getTooltipHintLangKey(PreviewContext context) {
    return "shulkerboxtooltip.hint.sorted";
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
