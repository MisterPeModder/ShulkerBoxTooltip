package com.misterpemodder.shulkerboxtooltip.impl.color;

import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ColorKeyImpl(float[] rgbComponents, float[] defaultRgbComponents) implements ColorKey {
  private static final Codec<Pair<Identifier, String>> CATEGORY_AND_ID_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Identifier.CODEC.fieldOf("category").forGetter(Pair::getFirst),
          Codec.STRING.fieldOf("id").forGetter(Pair::getSecond)).apply(instance, Pair::of));

  private static final Codec<ColorKey> FULL_CODEC = CATEGORY_AND_ID_CODEC.flatXmap(
      (Pair<Identifier, String> categoryAndId) -> {
        Identifier category = categoryAndId.getFirst();
        String id = categoryAndId.getSecond();
        @Nullable ColorKey key = ColorRegistryImpl.INSTANCE.category(category).key(id);

        if (key == null) {
          return DataResult.error(() -> "Unknown color key " + id + " in category " + category);
        } else {
          return DataResult.success(key);
        }
      }, (ColorKey key) -> DataResult.error(() -> "Cannot encode color key " + key + " as a string"));

  private static final Codec<ColorKey> INT_CODEC = Codec.INT.xmap(ColorKey::ofRgb, ColorKey::rgb);
  public static final Codec<ColorKey> CODEC = Codec.withAlternative(FULL_CODEC, INT_CODEC);

  @Override
  public int rgb() {
    return ShulkerBoxTooltipUtil.componentsToRgb(this.rgbComponents());
  }

  @Override
  public int defaultRgb() {
    return ShulkerBoxTooltipUtil.componentsToRgb(this.defaultRgbComponents());
  }

  @Override
  public void setRgb(int rgb) {
    this.setRgb(ShulkerBoxTooltipUtil.rgbToComponents(rgb));
  }

  @Override
  public void setRgb(float[] rgb) {
    this.rgbComponents[0] = rgb[0];
    this.rgbComponents[1] = rgb[1];
    this.rgbComponents[2] = rgb[2];
  }

  @Override
  public String toString() {
    return String.format("ColorKey(rgb=#%x, defaultRgb=#%x)", this.rgb(), this.defaultRgb());
  }
}
