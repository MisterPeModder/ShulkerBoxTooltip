package com.misterpemodder.shulkerboxtooltip.impl.config.validators;

import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Function;

public final class GreaterThanZero implements Function<Object, Optional<Text>> {
  @Override
  public Optional<Text> apply(Object value) {
    Class<?> valueClass = value.getClass();
    if (valueClass.equals(Integer.class) && (Integer) value <= 0) {
      return Optional.of(Text.translatableWithFallback("shulkerboxtooltip.config.validator.greater_than_zero",
          "Must be greater than zero"));
    }
    return Optional.empty();
  }
}
