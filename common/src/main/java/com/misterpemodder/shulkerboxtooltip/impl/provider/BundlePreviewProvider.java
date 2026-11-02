package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.config.Theme;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.api.renderer.RenderContext;
import com.misterpemodder.shulkerboxtooltip.impl.config.BundleTheme;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
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
    if (filledContainer(context) != null)
      return true;
    if (ShulkerBoxTooltip.config.preview.themeBundle == BundleTheme.VANILLA) {
      return false;
    }
    var bundleContents = context.stack().get(DataComponents.BUNDLE_CONTENTS);
    return bundleContents != null && bundleContents.items().iterator().hasNext();
  }

  @Override
  @Environment(EnvType.CLIENT)
  public Theme getTheme() {
    return bundleTheme();
  }

  @Override
  @Environment(EnvType.CLIENT)
  public PreviewRenderer getRenderer() {
    return BundlePreviewRenderer.INSTANCE;
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    ItemContainerContents container = filledContainer(context);
    if (container != null)
      return container.size();
    return 64;
  }

  @Override
  public int getActiveSlotCount(PreviewContext context) {
    if (filledContainer(context) != null)
      return this.getInventoryMaxSize(context);
    var bundleContents = context.stack().getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
    return bundleContents.size();
  }

  @Override
  public int getSelectedSlot(PreviewContext context) {
    if (filledContainer(context) != null)
      return -1;

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
    ItemContainerContents container = filledContainer(context);
    if (container != null) {
      var inv = NonNullList.withSize(container.size(), ItemStack.EMPTY);
      container.copyInto(inv);
      return inv;
    }

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
    if (filledContainer(context) != null)
      return "shulkerboxtooltip.hint.compact";
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
    if (filledContainer(context) != null)
      return global <= 0 ? 9 : global;
    return (global == 9) ? 8 : global;
  }

  @Override
  public int getCompactMaxRowSize(PreviewContext context) {
    return this.getMaxRowSize(context);
  }

  /**
   * A bundle that carries {@code minecraft:container} previews those slots, like a shulker box.
   * An empty component falls through to the vanilla bundle contents.
   */
  private static ItemContainerContents filledContainer(PreviewContext context) {
    ItemContainerContents container = context.stack().get(DataComponents.CONTAINER);
    if (container == null || container.size() <= 0)
      return null;
    return container;
  }

  @Environment(EnvType.CLIENT)
  private static Theme bundleTheme() {
    return switch (ShulkerBoxTooltip.config.preview.themeBundle) {
      case SHULKERBOXTOOLTIP -> Theme.SHULKERBOXTOOLTIP;
      case VANILLA, VANILLA_PLUS -> Theme.VANILLA;
    };
  }

  /**
   * Picks the shulker-style window for bundles that store a container, and keeps the bundle theme otherwise.
   * {@link PreviewProvider#getTheme()} has no stack, so the choice happens when the preview is bound.
   */
  @Environment(EnvType.CLIENT)
  private static final class BundlePreviewRenderer implements PreviewRenderer {
    private static final BundlePreviewRenderer INSTANCE = new BundlePreviewRenderer();

    private PreviewRenderer delegate = PreviewRenderer.getDefaultRendererInstance();

    @Override
    public int getHeight() {
      return this.delegate.getHeight();
    }

    @Override
    public int getWidth() {
      return this.delegate.getWidth();
    }

    @Override
    public void setPreview(PreviewContext context, PreviewProvider provider) {
      Theme theme = filledContainer(context) != null ? ShulkerBoxTooltip.config.preview.theme : bundleTheme();
      this.delegate = PreviewRenderer.getDefaultRendererInstance(theme);
      this.delegate.setPreview(context, provider);
    }

    @Override
    public void setPreviewType(PreviewType type) {
      this.delegate.setPreviewType(type);
    }

    @Override
    public void draw(RenderContext context) {
      this.delegate.draw(context);
    }

    @Override
    public int getOutsideXOffset() {
      return this.delegate.getOutsideXOffset();
    }

    @Override
    public int getOutsideYOffset() {
      return this.delegate.getOutsideYOffset();
    }
  }
}
