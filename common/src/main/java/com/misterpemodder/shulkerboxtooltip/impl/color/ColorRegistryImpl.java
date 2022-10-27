package com.misterpemodder.shulkerboxtooltip.impl.color;

import com.google.common.base.Preconditions;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class ColorRegistryImpl implements ColorRegistry {
  private final Map<Identifier, ColorRegistryImpl.Category> categories;
  private final Map<Identifier, ColorRegistryImpl.Category> emptyCategories;

  private final Map<Identifier, ColorRegistry.Category> categoriesView;
  private boolean locked;
  private int registeredKeysCount;

  public static final ColorRegistryImpl INSTANCE = new ColorRegistryImpl();

  public ColorRegistryImpl() {
    this.categories = new HashMap<>();
    this.emptyCategories = new HashMap<>();

    this.categoriesView = Collections.unmodifiableMap(this.categories);
    this.locked = true;
    this.registeredKeysCount = 0;
  }

  @Override
  @Nonnull
  public ColorRegistryImpl.Category category(Identifier categoryId) {
    var category = this.categories.get(categoryId);
    if (category == null)
      return this.emptyCategories.computeIfAbsent(categoryId, Category::new);
    return category;
  }

  @Override
  @Nonnull
  public ColorRegistry.Category defaultCategory() {
    return this.category(ShulkerBoxTooltipUtil.id("default"));
  }

  @Override
  @Nonnull
  public Map<Identifier, ColorRegistry.Category> categories() {
    return this.categoriesView;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public void resetRegisteredKeysCount() {
    this.registeredKeysCount = 0;
  }

  public int registeredKeysCount() {
    return this.registeredKeysCount;
  }

  public final class Category implements ColorRegistry.Category {
    private final Identifier id;
    /**
     * The map of color keys in this category, initialized on first color register.
     */
    private Map<String, ColorKey> keys = null;
    private Map<ColorKey, String> unlocalizedNames = Collections.emptyMap();
    private Map<String, ColorKey> keysView = Collections.emptyMap();
    /**
     * RGB values to assign for each color when they get registered.
     * Only initialized when {@link #setRgbKeyLater(String, int)} is called.
     */
    private Map<String, Integer> lateKeyValues = null;

    public Category(Identifier id) {
      this.id = id;
    }

    @Nullable
    @Override
    public ColorKey key(String colorId) {
      return this.keysView.get(colorId);
    }

    public void setRgbKeyLater(String colorId, int rgb) {
      if (this.lateKeyValues == null)
        this.lateKeyValues = new HashMap<>();
      this.lateKeyValues.put(colorId, rgb);
    }

    @Nullable
    @Override
    public String keyUnlocalizedName(ColorKey key) {
      return this.unlocalizedNames.get(key);
    }

    @Override
    public ColorRegistry.Category register(ColorKey key, String colorId, @Nullable String unlocalizedName) {
      Preconditions.checkNotNull(key, "cannot register null color key");
      Preconditions.checkNotNull(colorId, "cannot register null color ID");

      if (ColorRegistryImpl.this.locked)
        throw new IllegalStateException(
            "Cannot register color keys outside the scope of ShulkerBoxTooltipApi.registerColors()");

      this.registerSelf();
      this.registerKey(key, colorId, unlocalizedName);
      this.setLateKeyValue(key, colorId);

      return this;
    }

    /**
     * Registers the current category at the registry if not already registered.
     */
    private void registerSelf() {
      if (this.keys != null)
        return;
      this.keys = new LinkedHashMap<>();
      this.unlocalizedNames = new HashMap<>();
      this.keysView = Collections.unmodifiableMap(this.keys);
      ColorRegistryImpl.this.categories.put(this.id, this);
      ColorRegistryImpl.this.emptyCategories.remove(this.id);
    }

    private void registerKey(ColorKey key, String colorId, @Nullable String unlocalizedName) {
      if (this.keys.containsKey(colorId))
        ShulkerBoxTooltip.LOGGER.warn(
            "[" + ShulkerBoxTooltip.MOD_NAME + "] Overriding color key " + colorId + " for category " + this.id);
      if (unlocalizedName == null)
        unlocalizedName =
            "shulkerboxtooltip.colors." + this.id.getNamespace() + "." + this.id.getPath() + "." + colorId;

      this.keys.put(colorId, key);
      this.unlocalizedNames.put(key, unlocalizedName);
      ++ColorRegistryImpl.this.registeredKeysCount;
    }

    private void setLateKeyValue(ColorKey key, String colorId) {
      if (this.lateKeyValues != null && this.lateKeyValues.containsKey(colorId)) {
        // when a value was assigned to this key before its registration
        key.setRgb(this.lateKeyValues.get(colorId));
        this.lateKeyValues.remove(colorId);
      }
    }

    @Override
    public Map<String, ColorKey> keys() {
      return this.keysView;
    }
  }
}
