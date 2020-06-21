package com.misterpemodder.shulkerboxtooltip.impl.util;

import java.util.Optional;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;

public class DefaultedTranslatableText extends TranslatableText {
  private final String defaultString;
  private final StringRenderable defaultStringRenderable;

  public DefaultedTranslatableText(String key, String defaultString) {
    super(key);
    this.defaultString = defaultString;
    this.defaultStringRenderable = StringRenderable.plain(defaultString);
  }

  public DefaultedTranslatableText(String key, String defaultString, Object... args) {
    super(key, args);
    this.defaultString = defaultString;
    this.defaultStringRenderable = StringRenderable.plain(defaultString);
  }

  public String getDefaultString() {
    return this.defaultString;
  }

  @Override
  @Environment(EnvType.CLIENT)
  public <T> Optional<T> visitSelf(StyledVisitor<T> visitor, Style style) {
    String key = this.getKey();

    if (Language.getInstance().get(key).equals(key))
      return defaultStringRenderable.visit(visitor, style);
    return super.visitSelf(visitor, style);
  }

  @Override
  public <T> Optional<T> visitSelf(Visitor<T> visitor) {
    String key = this.getKey();

    if (Language.getInstance().get(key).equals(key))
      return defaultStringRenderable.visit(visitor);
    return super.visitSelf(visitor);
  }
}