package com.misterpemodder.shulkerboxtooltip.api.color;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Provides a way to register custom color keys and categories to allow user-customization through the mod's config.
 *
 * @since 3.2.0
 */
@ApiStatus.NonExtendable
@Environment(EnvType.CLIENT)
public interface ColorRegistry {
  /**
   * Access to a category, the returned instance will only be registered if at least one color key is registered to it.
   * <p>
   * In the GUI, the category will be represented by a sub-list in the 'Colors' category of the config screen.
   * The name of the category is obtained by localizing the string {@code shulkerboxtooltip.colors.MOD_ID.CATEGORY_ID},
   * Where {@code MOD_ID} is the namespace of {@code categoryId}, and {@code CATEGORY_ID} is the path of {@code categoryId}.
   *
   * @param categoryId The unique identifier of the category.
   * @return The category.
   * @since 3.2.0
   */
  @Nonnull
  Category category(Identifier categoryId);

  /**
   * Access to the default category.
   * <p>
   * In the GUI, the keys belonging the default category will appear at the root of the 'Colors' category,
   * before other color category sub-lists.
   *
   * @return The default category instance.
   * @since 3.2.0
   */
  @Nonnull
  Category defaultCategory();

  /**
   * Access to all the registered categories.
   *
   * @return An <b>immutable</b> view over the existing categories.
   * @since 3.2.0
   */
  @Nonnull
  Map<Identifier, Category> categories();

  /**
   * A color category.
   *
   * @since 3.2.0
   */
  interface Category {
    /**
     * Gets a color key registered to this category with the given id, or {@code null} if not found.
     *
     * @param colorId The identifier of the color.
     * @return The {@link ColorKey} instance linked to the given id, or {@code null} if not found.
     * @since 3.2.0
     */
    @Nullable
    ColorKey key(String colorId);

    /**
     * Gets the localization key of the given color key.
     *
     * @param key The color key.
     * @return The unlocalized name of the given color key.
     * @since 3.2.0
     */
    @Nullable
    String keyUnlocalizedName(ColorKey key);

    /**
     * Registers a color key.
     * <p>
     * The GUI name of the color key is obtained by localizing
     * the string {@code shulkerboxtooltip.colors.MOD_ID.CATEGORY_ID.KEY_ID},
     * Where {@code MOD_ID} is the namespace of the category's ID, {@code CATEGORY_ID} is the path of the category's ID,
     * and {@code KEY_ID} is the {@code colorId} parameter.
     *
     * @param key     The color key to register.
     * @param colorId The name of this color key.
     * @return This category instance to allow chaining.
     * @since 3.2.0
     */
    default Category register(ColorKey key, String colorId) {
      return this.register(key, colorId, null);
    }

    /**
     * Registers a color key.
     * <p>
     * The GUI name of the color key is obtained by localizing
     * the string {@code unlocalizedName}, or when the parameter is null,
     * {@code shulkerboxtooltip.colors.MOD_ID.CATEGORY_ID.KEY_ID}, Where {@code MOD_ID} is the namespace of the category's ID,
     * {@code CATEGORY_ID} is the path of the category's ID, and {@code KEY_ID} is the {@code colorId} parameter.
     *
     * @param key             The color key to register.
     * @param colorId         The name of this color key.
     * @param unlocalizedName The unlocalized name of the key, pass {@code null} to use the default name.
     * @return This category instance to allow chaining.
     * @since 3.2.0
     */
    Category register(ColorKey key, String colorId, @Nullable String unlocalizedName);

    /**
     * Access the all the color keys registered to this category.
     *
     * @return An <b>immutable</b> view over the existing keys in this category.
     * @since 3.2.0
     */
    Map<String, ColorKey> keys();
  }
}
