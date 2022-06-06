package com.misterpemodder.shulkerboxtooltip.impl.util;

import java.util.Optional;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Language;

public class DefaultedTranslatableText extends TranslatableTextContent {
  private final String defaultString;
  private final StringVisitable defaultStringRenderable;

  public DefaultedTranslatableText(String key, String defaultString) {
    super(key);
    this.defaultString = defaultString;
    this.defaultStringRenderable = StringVisitable.plain(defaultString);
  }

  public DefaultedTranslatableText(String key, String defaultString, Object... args) {
    super(key, args);
    this.defaultString = defaultString;
    this.defaultStringRenderable = StringVisitable.plain(defaultString);
  }

  public String getDefaultString() {
    return this.defaultString;
  }

  @Override
  @Environment(EnvType.CLIENT)
  public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
    String key = this.getKey();

    if (Language.getInstance().get(key).equals(key))
      return defaultStringRenderable.visit(visitor, style);
    return super.visit(visitor, style);
  }

  @Override
  public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
    String key = this.getKey();

    if (Language.getInstance().get(key).equals(key))
      return defaultStringRenderable.visit(visitor);
    return super.visit(visitor);
  }
}
