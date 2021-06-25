package com.misterpemodder.shulkerboxtooltip.impl.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.text.Text;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Validator {
  Class<? extends Function<Object, Optional<Text>>> value();
}
