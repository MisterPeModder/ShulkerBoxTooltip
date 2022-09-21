package com.misterpemodder.shulkerboxtooltip.impl.color;

import com.google.common.base.Preconditions;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class ColorRegistryImpl implements ColorRegistry {
  private final Map<Identifier, ColorRegistry.Category> categories;
  private final Map<Identifier, ColorRegistry.Category> categoriesView;
  private boolean locked;
  private int registeredKeysCount;

  public static final ColorRegistryImpl INSTANCE = new ColorRegistryImpl();

  public ColorRegistryImpl() {
    this.categories = new HashMap<>();
    this.categoriesView = Collections.unmodifiableMap(this.categories);
    this.locked = true;
    this.registeredKeysCount = 0;
  }

  @Override
  public ColorRegistry.Category category(Identifier categoryId) {
    return this.categories.getOrDefault(categoryId, new Category(categoryId));
  }

  @Override
  public ColorRegistry.Category defaultCategory() {
    return this.category(ShulkerBoxTooltipUtil.id("default"));
  }

  @Override
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

  private class Category implements ColorRegistry.Category {
    private final Identifier id;
    private Map<String, ColorKey> keys = null;
    private Map<ColorKey, String> unlocalizedNames = Collections.emptyMap();
    private Map<String, ColorKey> keysView = Collections.emptyMap();

    public Category(Identifier id) {
      this.id = id;
    }

    @Nullable
    @Override
    public ColorKey key(String colorId) {
      return this.keysView.get(colorId);
    }

    @Nullable
    @Override
    public String keyUnlocalizedName(ColorKey key) {
      return this.unlocalizedNames.get(key);
    }

    @Override
    public ColorRegistry.Category register(ColorKey key, String colorId, String unlocalizedName) {
      Preconditions.checkNotNull(key, "cannot register null color key");

      if (ColorRegistryImpl.this.locked)
        throw new IllegalStateException(
            "Cannot register color keys outside the scope of ShulkerBoxTooltipApi.registerColors()");
      if (this.keys == null) {
        // only register this category when a key is registered
        this.keys = new LinkedHashMap<>();
        this.unlocalizedNames = new HashMap<>();
        this.keysView = Collections.unmodifiableMap(this.keys);
        ColorRegistryImpl.this.categories.put(this.id, this);
      }
      if (this.keys.containsKey(colorId))
        ShulkerBoxTooltip.LOGGER.warn(
            "[" + ShulkerBoxTooltip.MOD_NAME + "] Overriding color key " + colorId + " for category " + this.id);
      if (unlocalizedName == null)
        unlocalizedName = "shulkerboxtooltip.colors." + this.id.getNamespace() + "." + this.id.getPath() + "." + colorId;

      this.keys.put(colorId, key);
      this.unlocalizedNames.put(key, unlocalizedName);
      ++ColorRegistryImpl.this.registeredKeysCount;
      return this;
    }

    @Override
    public Map<String, ColorKey> keys() {
      return this.keysView;
    }
  }
}
