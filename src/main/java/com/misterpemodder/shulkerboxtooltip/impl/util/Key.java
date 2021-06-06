package com.misterpemodder.shulkerboxtooltip.impl.util;

import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;

public final class Key {
  public static final Key UNKNOWN_KEY = new Key(InputUtil.UNKNOWN_KEY);

  @Environment(EnvType.CLIENT)
  InputUtil.Key inner;

  @Environment(EnvType.CLIENT)
  public Key(InputUtil.Key key) {
    this.inner = key;
  }

  @Environment(EnvType.CLIENT)
  public InputUtil.Key get() {
    return this.inner;
  }

  @Environment(EnvType.CLIENT)
  public void set(InputUtil.Key key) {
    this.inner = key;
  }

  @Nullable
  public static Key defaultPreviewKey() {
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
      return new Key(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_LEFT_SHIFT));
    return null;
  }

  @Nullable
  public static Key defaultFullPreviewKey() {
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
      return new Key(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_LEFT_ALT));
    return null;
  }

  public static Key fromTranslationKey(@Nullable String translationKey) {
    if (translationKey == null)
      return UNKNOWN_KEY;
    try {
      return new Key(InputUtil.fromTranslationKey(translationKey));
    } catch (Exception e) {
      return UNKNOWN_KEY;
    }
  }
}
