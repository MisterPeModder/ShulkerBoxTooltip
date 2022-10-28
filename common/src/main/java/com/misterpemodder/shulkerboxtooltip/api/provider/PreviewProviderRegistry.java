package com.misterpemodder.shulkerboxtooltip.api.provider;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.provider.PreviewProviderRegistryImpl;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * Provides a way to register preview providers for items and a way to access them.
 *
 * @since 3.0.0
 */
@ApiStatus.NonExtendable
public interface PreviewProviderRegistry {
  /**
   * Gets the preview provider registry instance.
   *
   * @return The registry instance.
   * @since 3.0.0
   */
  @Nonnull
  static PreviewProviderRegistry getInstance() {
    return PreviewProviderRegistryImpl.INSTANCE;
  }

  /**
   * Registers a {@link PreviewProvider}.
   * New preview providers can only be registered inside the {@link ShulkerBoxTooltipApi#registerProviders(PreviewProviderRegistry)} method.
   *
   * @param id       The id of the preview provider.
   * @param provider The provider.
   * @param items    The items for which this provider will work.
   *                 When multiple providers are registered for the same item,
   *                 the one with the highest priority is chosen.
   * @since 3.0.0
   */
  void register(Identifier id, PreviewProvider provider, Iterable<Item> items);

  /**
   * Registers a {@link PreviewProvider}.
   * New preview providers can only be registered inside the {@link ShulkerBoxTooltipApi#registerProviders(PreviewProviderRegistry)} method.
   *
   * @param id       The id of the preview provider.
   * @param provider The provider.
   * @param items    The items for which this provider will work.
   *                 When multiple providers are registered for the same item,
   *                 the one with the highest priority is chosen.
   * @since 3.0.0
   */
  void register(Identifier id, PreviewProvider provider, Item... items);

  /**
   * Attempts to get the corresponding preview provider associated with the given item stack.
   *
   * @param id The id the provider was registered with.
   * @return The associated provider, can be null.
   * @since 3.0.0
   */
  @Nullable
  PreviewProvider get(Identifier id);

  /**
   * Gets the associated provider for the given item stack.
   *
   * @param stack The target item stack.
   * @return The associated provider, or {@code null} if the stack does not have a preview provider.
   * @since 3.0.0
   */
  @Nullable
  PreviewProvider get(ItemStack stack);

  /**
   * Gets the associated provider for the given item.
   *
   * @param item The target item.
   * @return The associated provider, or {@code null} if the item does not have a preview provider.
   * @since 3.0.0
   */
  @Nullable
  PreviewProvider get(Item item);

  /**
   * Attempts to get the corresponding identifier associated with the given preview provider.
   *
   * @param provider The preview provider
   * @return The id of given provider, or {@code null} if it was not registered.
   * @since 3.0.0
   */
  @Nullable
  Identifier getId(PreviewProvider provider);

  /**
   * Returns the set of items the given {@link PreviewProvider} works with.
   *
   * <p>The set of items may be smaller than the one given in {@link #register(Identifier, PreviewProvider, Iterable)}
   * if the provider's priority was lower than other providers for each missing item.
   *
   * @param provider The preview provider
   * @return The immutable set of items, will be empty if provider was not registered.
   * @since 3.0.0
   */
  @Nonnull
  Set<Item> getItems(PreviewProvider provider);

  /**
   * Gets the set of all registered {@link PreviewProvider preview providers}.
   *
   * @return The set of all registered preview providers.
   * @since 3.0.0
   */
  @Nonnull
  Set<PreviewProvider> getProviders();

  /**
   * Gets the set of all the registered identifiers.
   *
   * @return the set of all registered ids.
   * @since 3.0.0
   */
  @Nonnull
  Set<Identifier> getIds();
}
