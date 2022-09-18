package com.misterpemodder.shulkerboxtooltip.api.color;

import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Provides a way to register custom color keys and categories to allow user-customization through the mod's config.
 *
 * @since 3.2.0
 */
public interface ColorRegistry {
  /**
   * Access to a category, the returned instance will only be registered if at least one color key is registered to it.
   * <p>
   * In the GUI, the category will be represented by a sub-list in the 'Colors' category of the config screen.
   * The name of the category is obtained by localizing the string {@code shulkerboxtooltip.colors.MOD_ID.CATEGORY_ID},
   * where {@code MOD_ID} is the namespace of {@code categoryId} and {@code CATEGORY_ID} is the path of {@code categoryId}.
   *
   * @return The category.
   * @since 3.2.0
   */
  Category category(Identifier categoryId);

  /**
   * Access to the default category.
   * <p>
   * In the GUI, the keys belonging the default category will appear at the root of the 'Colors' category,
   * before other color category sub-lists.
   *
   * @since 3.2.0
   */
  Category defaultCategory();

  /**
   * @return An <b>immutable</b> view over the existing categories.
   * @since 3.2.0
   */
  Map<Identifier, Category> categories();

  /**
   * A color category.
   *
   * @since 3.2.0
   */
  interface Category {
    /**
     * @since 3.2.0
     */
    @Nullable
    ColorKey get(String colorId);

    /**
     * @return This category instance to allow chaining.
     * @since 3.2.0
     */
    Category register(String colorId, ColorKey key);

    /**
     * @return An <b>immutable</b> view over the existing keys in this category.
     * @since 3.2.0
     */
    Map<String, ColorKey> keys();
  }
}
