package com.misterpemodder.shulkerboxtooltip.impl.tree;

import com.google.common.collect.ImmutableList;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.ConfigCategory;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.RequiresRestart;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.Synchronize;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.Validator;
import com.misterpemodder.shulkerboxtooltip.impl.tree.ValueConfigNode.ValueReader;
import com.misterpemodder.shulkerboxtooltip.impl.tree.ValueConfigNode.ValueWriter;
import com.misterpemodder.shulkerboxtooltip.impl.util.EnvironmentUtil;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Arrays;
import java.util.Comparator;

import static com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil.id;

public final class RootConfigNode<C> implements ConfigNode<C> {

  public static final Component TITLE = Component.translatable("shulkerboxtooltip.config.title");

  private ImmutableList<CategoryConfigNode<C>> categories;

  private RootConfigNode(ImmutableList<CategoryConfigNode<C>> categories) {
    this.categories = categories;
  }

  public static <C> RootConfigNode<C> create(C defaultConfig) {
    return new Builder<>(defaultConfig).build();
  }

  public void reload(C defaultConfig) {
    this.categories = new Builder<>(defaultConfig).build().getCategories();
  }

  @NotNull
  @Override
  public String getName() {
    return "";
  }

  @NotNull
  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Nullable
  @Override
  public Component getTooltip() {
    return null;
  }

  @Nullable
  @Override
  public Component getPrefix() {
    return null;
  }

  @Override
  public void resetToDefault() {
    this.categories.forEach(ConfigNode::resetToDefault);
  }

  @Override
  public void resetToActive(C config) {
    this.categories.forEach(category -> category.resetToActive(config));
  }

  @Override
  public boolean restartRequired(C config) {
    return this.categories.stream().anyMatch(categoryConfigNode -> categoryConfigNode.restartRequired(config));
  }

  @Override
  public boolean isDefaultValue(C config) {
    return this.categories.stream().allMatch(node -> node.isDefaultValue(config));
  }

  @Override
  public boolean isActiveValue(C config) {
    return this.categories.stream().allMatch(node -> node.isActiveValue(config));
  }

  @Nullable
  @Override
  public Component validate(C config) {
    Component error = null;

    for (var node : this.categories) {
      var result = node.validate(config);
      if (result != null) {
        if (error != null) {
          return CategoryConfigNode.MULTIPLE_ERRORS;
        }
        error = result;
      }
    }
    return error;
  }

  @NotNull
  public ImmutableList<CategoryConfigNode<C>> getCategories() {
    return this.categories;
  }

  @Override
  public void writeToNbt(C config, CompoundTag compound) {
    this.categories.forEach(node -> node.writeToNbt(config, compound));
  }

  @Override
  public void readFromNbt(C config, CompoundTag compound) {
    this.categories.forEach(node -> node.readFromNbt(config, compound));
  }

  @Override
  public void copy(C from, C to) {
    this.categories.forEach(node -> node.copy(from, to));
  }

  @Override
  public void writeEditingToConfig(C config) {
    this.categories.forEach(node -> node.writeEditingToConfig(config));
  }

  private static class Builder<C> {
    private Object defaultConfig;

    private Builder(C defaultConfig) {
      this.defaultConfig = defaultConfig;
    }

    @NotNull
    public RootConfigNode<C> build() {
      Class<?> configClass = this.defaultConfig.getClass();
      ImmutableList<CategoryConfigNode<C>> categories = Arrays.stream(configClass.getFields()) //
          .filter(field -> field.isAnnotationPresent(ConfigCategory.class)) //
          .map(field -> Pair.of(field.getAnnotation(ConfigCategory.class).ordinal(), field)) //
          .sorted(Comparator.comparingInt(Pair::getFirst))
          .map(pair -> this.createCategoryNode(pair.getSecond()))
          .collect(ImmutableList.toImmutableList());

      this.defaultConfig = null;
      return new RootConfigNode<>(categories);
    }

    private CategoryConfigNode<C> createCategoryNode(Field categoryField) {
      Object defaultCategory;

      try {
        categoryField.setAccessible(true);
        defaultCategory = categoryField.get(this.defaultConfig);
      } catch (IllegalAccessException | InaccessibleObjectException | SecurityException e) {
        throw new IllegalArgumentException("Failed to get category field", e);
      }
      var categoryClass = categoryField.getType();
      var categoryName = categoryField.getName();
      var categoryBuilder = CategoryConfigNode.<C>builder().name(categoryName).title(
          Component.translatable("shulkerboxtooltip.config.category." + ShulkerBoxTooltipUtil.snakeCase(categoryName)));

      for (var valueField : categoryClass.getDeclaredFields()) {
        this.addValueNode(defaultCategory, categoryField, valueField, categoryBuilder);
      }
      return categoryBuilder.build();
    }

    private void addValueNode(Object defaultCategory, Field categoryField, Field valueField,
        CategoryConfigNode.Builder<C> categoryBuilder) {
      Object defaultValue;

      try {
        valueField.setAccessible(true);
        defaultValue = valueField.get(defaultCategory);
      } catch (IllegalAccessException | InaccessibleObjectException | SecurityException e) {
        throw new IllegalArgumentException("Failed to get value field", e);
      }

      if (EnvironmentUtil.isClient() && defaultValue instanceof ColorRegistry colorRegistry) {
        this.addColorRegistryField(colorRegistry, categoryBuilder);
        return;
      }

      this.addSingleValueField(defaultValue.getClass(), defaultValue, categoryField, valueField, categoryBuilder);
    }

    private <T> void addSingleValueField(Class<? extends T> type, T defaultValue, Field categoryField, Field valueField,
        CategoryConfigNode.Builder<C> categoryBuilder) {
      var valueName = valueField.getName();
      var titleKey = "shulkerboxtooltip.config.option." + ShulkerBoxTooltipUtil.snakeCase(categoryField.getName()) + "."
          + ShulkerBoxTooltipUtil.snakeCase(valueName);
      var title = Component.translatable(titleKey);
      var tooltip = Component.translatable(titleKey + ".tooltip");
      var prefixKey = titleKey + ".prefix";

      categoryBuilder.<T, T>value(valueBuilder -> {
        valueBuilder //
            .type(type)
            .valueType(type)
            .name(valueName)
            .title(title)
            .tooltip(tooltip)
            .defaultValue(defaultValue)
            .valueReader(this.makeValueReader(type, categoryField, valueField))
            .valueWriter(this.makeValueWriter(type, categoryField, valueField))
            .requiresRestart(valueField.isAnnotationPresent(RequiresRestart.class));

        if (EnvironmentUtil.isClient() && I18n.exists(prefixKey))
          valueBuilder.prefix(Component.translatable(prefixKey));

        if (valueField.isAnnotationPresent(Synchronize.class))
          valueBuilder //
              .nbtReader(this.makeNbtReader(type, valueField.getName(), defaultValue)) //
              .nbtWriter(this.makeNbtWriter(valueField.getName(), defaultValue));

        var validatorAnnotation = valueField.getAnnotation(Validator.class);
        if (validatorAnnotation != null) {
          valueBuilder.validator(this.makeValueValidator(validatorAnnotation, valueField));
        }

        return valueBuilder;
      });
    }

    @Environment(EnvType.CLIENT)
    private void addColorRegistryField(ColorRegistry colorRegistry, CategoryConfigNode.Builder<C> categoryBuilder) {
      this.addColorRegistryCategoryNode(colorRegistry.defaultCategory(), id("default"), categoryBuilder);
      for (var entry : colorRegistry.categories().entrySet()) {
        var categoryId = entry.getKey();
        var colorCategory = entry.getValue();
        if (colorCategory == colorRegistry.defaultCategory())
          continue;
        this.addColorRegistryCategoryNode(colorCategory, categoryId, categoryBuilder);
      }
    }

    @Environment(EnvType.CLIENT)
    private void addColorRegistryCategoryNode(ColorRegistry.Category colorCategory, ResourceLocation categoryId,
        CategoryConfigNode.Builder<C> categoryBuilder) {
      categoryBuilder.category(subCategoryBuilder -> {
        var titleKey = "shulkerboxtooltip.colors." + categoryId.getNamespace() + "." + categoryId.getPath();
        var title = Component.translatable(titleKey);

        subCategoryBuilder.name(categoryId.toString()).title(title);
        for (var entry : colorCategory.keys().entrySet()) {
          this.addColorKeyValueNode(entry.getValue(), entry.getKey(), colorCategory, subCategoryBuilder);
        }
        return subCategoryBuilder;
      });
    }

    @Environment(EnvType.CLIENT)
    private void addColorKeyValueNode(ColorKey colorKey, String colorKeyId, ColorRegistry.Category colorCategory,
        CategoryConfigNode.Builder<C> subCategoryBuilder) {
      var titleKey = colorCategory.keyUnlocalizedName(colorKey);

      subCategoryBuilder.<ColorKey, Integer>value(valueBuilder -> valueBuilder //
          .type(ColorKey.class)
          .valueType(Integer.class) //
          .name(colorKeyId)
          .title(titleKey == null ? Component.literal(colorKeyId) : Component.translatable(titleKey))
          .defaultValue(colorKey.defaultRgb())
          .valueReader(s -> colorKey.rgb())
          .valueWriter((s, v) -> colorKey.setRgb(v))
          .validator(v -> {
            if (v == null || (v & 0xFF000000) != 0)
              return Component.translatable("shulkerboxtooltip.config.validator.invalid_color");
            return null;
          }));
    }


    private <T> ValueReader<C, T> makeValueReader(Class<? extends T> type, Field categoryField, Field valueField) {
      try {
        valueField.setAccessible(true);
      } catch (InaccessibleObjectException | SecurityException e) {
        throw new IllegalArgumentException("Failed to set value field accessible", e);
      }

      return (C config) -> {
        try {
          return type.cast(valueField.get(categoryField.get(config)));
        } catch (IllegalAccessException | ClassCastException e) {
          throw new IllegalArgumentException("Failed to get value field", e);
        }
      };
    }

    private <T> ValueWriter<C, T> makeValueWriter(Class<? extends T> type, Field categoryField, Field valueField) {
      try {
        valueField.setAccessible(true);
      } catch (InaccessibleObjectException | SecurityException e) {
        throw new IllegalArgumentException("Failed to set value field accessible", e);
      }

      return (C config, T value) -> {
        try {
          valueField.set(categoryField.get(config), type.cast(value));
        } catch (IllegalAccessException | ClassCastException e) {
          throw new IllegalArgumentException("Failed to set value field", e);
        }
      };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> ValueReader<CompoundTag, T> makeNbtReader(Class<? extends T> type, String valueName, T defaultValue) {
      return switch (defaultValue) {
        case Enum<?> ignored -> tag -> (T) Enum.valueOf((Class<? extends Enum>) type, tag.getString(valueName).orElse(((Enum) defaultValue).name()));
        case Boolean ignored -> tag -> (T) tag.getBoolean(valueName).orElse((Boolean) defaultValue);
        case String ignored -> tag -> (T) tag.getString(valueName).orElse((String) defaultValue);
        case Integer ignored -> tag -> (T) tag.getInt(valueName).orElse((Integer) defaultValue);
        default -> throw new IllegalArgumentException("Unsupported value type: " + defaultValue.getClass());
      };
    }

    private <T> ValueWriter<CompoundTag, T> makeNbtWriter(String valueName, T defaultValue) {
      return switch (defaultValue) {
        case Enum<?> ignored -> (tag, value) -> tag.putString(valueName, ((Enum<?>) value).name());
        case Boolean ignored -> (tag, value) -> tag.putBoolean(valueName, (Boolean) value);
        case String ignored -> (tag, value) -> tag.putString(valueName, (String) value);
        case Integer ignored -> (tag, value) -> tag.putInt(valueName, (Integer) value);
        default -> throw new IllegalArgumentException("Unsupported value type: " + defaultValue.getClass());
      };
    }


    @SuppressWarnings("unchecked")
    private <T> ValueConfigNode.ValueValidator<T> makeValueValidator(Validator validatorAnnotation, Field valueField) {
      try {
        return (ValueConfigNode.ValueValidator<T>) validatorAnnotation.value().getDeclaredConstructor().newInstance();
      } catch (ReflectiveOperationException e) {
        throw new IllegalArgumentException("Failed to create validator for config field " + valueField, e);
      }
    }
  }

}
