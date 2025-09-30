package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.color.ColorKeyImpl;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * A {@link PreviewProvider} that can override the behavior of another {@link PreviewProvider} depending on its custom data.
 */
public class OverridingPreviewProvider implements PreviewProvider {
  private final PreviewProvider delegate;
  private final PreviewOverrides overrides;

  private OverridingPreviewProvider(PreviewProvider delegate, PreviewOverrides overrides) {
    this.delegate = delegate;
    this.overrides = overrides;
  }

  private record PreviewOverrides(Optional<Boolean> shouldDisplay, Optional<Integer> inventoryMaxSize,
                                  Optional<Integer> maxRowSize, Optional<Integer> compactMaxRowSize,
                                  Optional<Boolean> fullPreviewAvailable, Optional<Boolean> showTooltipHints,
                                  Optional<String> tooltipHintLangKey, Optional<String> fullTooltipHintLangKey,
                                  Optional<String> lockKeyTooltipHintLangKey, Optional<ColorKey> windowColor,
                                  Optional<ResourceLocation> texture, Optional<Boolean> canInsertItems,
                                  Optional<Boolean> canExtractItems) {
    public static final MapCodec<PreviewOverrides> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.lenientOptionalFieldOf("should_display").forGetter(PreviewOverrides::shouldDisplay),
            Codec.INT.lenientOptionalFieldOf("inventory_max_size").forGetter(PreviewOverrides::inventoryMaxSize),
            Codec.INT.lenientOptionalFieldOf("max_row_size").forGetter(PreviewOverrides::maxRowSize),
            Codec.INT.lenientOptionalFieldOf("compact_max_row_size").forGetter(PreviewOverrides::compactMaxRowSize),
            Codec.BOOL.lenientOptionalFieldOf("full_preview_available").forGetter(PreviewOverrides::fullPreviewAvailable),
            Codec.BOOL.lenientOptionalFieldOf("show_tooltip_hints").forGetter(PreviewOverrides::showTooltipHints),
            Codec.STRING.lenientOptionalFieldOf("tooltip_hint_lang_key").forGetter(PreviewOverrides::tooltipHintLangKey),
            Codec.STRING.lenientOptionalFieldOf("full_tooltip_hint_lang_key")
                .forGetter(PreviewOverrides::fullTooltipHintLangKey),
            Codec.STRING.lenientOptionalFieldOf("lock_tooltip_hint_lang_key")
                .forGetter(PreviewOverrides::lockKeyTooltipHintLangKey),
            ColorKeyImpl.CODEC.lenientOptionalFieldOf("window_color").forGetter(PreviewOverrides::windowColor),
            ResourceLocation.CODEC.lenientOptionalFieldOf("texture").forGetter(PreviewOverrides::texture),
            Codec.BOOL.lenientOptionalFieldOf("can_insert_items").forGetter(PreviewOverrides::canInsertItems),
            Codec.BOOL.lenientOptionalFieldOf("can_extract_items").forGetter(PreviewOverrides::canExtractItems))
        .apply(instance, PreviewOverrides::new));
  }

  public static PreviewProvider maybeWrap(@Nullable PreviewProvider delegate, ItemStack stack) {
    if (delegate == null || delegate instanceof OverridingPreviewProvider)
      return delegate;
    CustomData custom = stack.get(DataComponents.CUSTOM_DATA);

    if (custom == null)
      return delegate;
    return custom.copyTag().read(ShulkerBoxTooltip.MOD_ID, PreviewOverrides.CODEC.codec()).map(
        overrides -> (PreviewProvider) new OverridingPreviewProvider(delegate, overrides)).orElse(delegate);
  }

  @Override
  public List<Component> addTooltip(PreviewContext context) {
    return this.delegate.addTooltip(context);
  }

  @Override
  public boolean shouldDisplay(PreviewContext context) {
    return this.overrides.shouldDisplay.orElseGet(() -> this.delegate.shouldDisplay(context));
  }

  @Override
  public List<ItemStack> getInventory(PreviewContext context) {
    return this.delegate.getInventory(context);
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return this.overrides.inventoryMaxSize.orElseGet(() -> this.delegate.getInventoryMaxSize(context));
  }

  @Override
  public int getMaxRowSize(PreviewContext context) {
    return this.overrides.maxRowSize.orElseGet(() -> this.delegate.getMaxRowSize(context));
  }

  @Override
  public int getCompactMaxRowSize(PreviewContext context) {
    return this.overrides.compactMaxRowSize.orElseGet(() -> this.delegate.getCompactMaxRowSize(context));
  }

  @Override
  public boolean isFullPreviewAvailable(PreviewContext context) {
    return this.overrides.fullPreviewAvailable.orElseGet(() -> this.delegate.isFullPreviewAvailable(context));
  }

  @Override
  public boolean showTooltipHints(PreviewContext context) {
    return this.overrides.showTooltipHints.orElseGet(() -> this.delegate.showTooltipHints(context));
  }

  @Override
  public String getTooltipHintLangKey(PreviewContext context) {
    return this.overrides.tooltipHintLangKey.orElseGet(() -> this.delegate.getTooltipHintLangKey(context));
  }

  @Override
  public String getFullTooltipHintLangKey(PreviewContext context) {
    return this.overrides.fullTooltipHintLangKey.orElseGet(() -> this.delegate.getFullTooltipHintLangKey(context));
  }

  @Override
  public String getLockKeyTooltipHintLangKey(PreviewContext context) {
    return this.overrides.lockKeyTooltipHintLangKey.orElseGet(
        () -> this.delegate.getLockKeyTooltipHintLangKey(context));
  }

  @Override
  @Environment(EnvType.CLIENT)
  public ColorKey getWindowColorKey(PreviewContext context) {
    return this.overrides.windowColor.orElseGet(() -> this.delegate.getWindowColorKey(context));
  }

  @Override
  @Environment(EnvType.CLIENT)
  public PreviewRenderer getRenderer() {
    return this.delegate.getRenderer();
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void onInventoryAccessStart(PreviewContext context) {
    this.delegate.onInventoryAccessStart(context);
  }

  @Override
  @Nullable
  @Environment(EnvType.CLIENT)
  public ResourceLocation getTextureOverride(PreviewContext context) {
    return this.overrides.texture.orElseGet(() -> this.delegate.getTextureOverride(context));
  }

  @Override
  public int getPriority() {
    return this.delegate.getPriority();
  }
}
