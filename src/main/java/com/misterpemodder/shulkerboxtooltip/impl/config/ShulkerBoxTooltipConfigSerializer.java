package com.misterpemodder.shulkerboxtooltip.impl.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonNull;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.SyntaxError;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;

public class ShulkerBoxTooltipConfigSerializer<T extends ConfigData>
    extends JanksonConfigSerializer<T> {
  private Config definition;
  private Class<T> configClass;
  private Jankson jankson;

  public ShulkerBoxTooltipConfigSerializer(Config definition, Class<T> configClass) {
    this(definition, configClass, buildJankson());
  }

  protected ShulkerBoxTooltipConfigSerializer(Config definition, Class<T> configClass,
      Jankson jankson) {
    super(definition, configClass,
        (me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson) (Object) jankson);
    this.definition = definition;
    this.configClass = configClass;
    this.jankson = jankson;
  }

  private static Jankson buildJankson() {
    Jankson.Builder builder = Jankson.builder();

    builder.registerPrimitiveTypeAdapter(Key.class, it -> {
      if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
        return null;
      return new Key(
          it instanceof String ? InputUtil.fromTranslationKey((String) it) : InputUtil.UNKNOWN_KEY);
    });
    builder.registerTypeAdapter(Key.class, o -> {
      if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
        return null;
      String code = ((JsonPrimitive) o.get("code")).asString();
      InputUtil.Key key =
          code.endsWith(".unknown") ? InputUtil.UNKNOWN_KEY : InputUtil.fromTranslationKey(code);

      return new Key(key == null ? InputUtil.UNKNOWN_KEY : key);
    });
    builder.registerSerializer(Key.class, (key, marshaller) -> {
      JsonObject object = new JsonObject();

      if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
        object.put("code", JsonNull.INSTANCE);
      else
        object.put("code", new JsonPrimitive(key.get().getTranslationKey()));
      return object;
    });
    return builder.build();
  }

  private Path getLegacyConfigPath() {
    return FabricLoader.getInstance().getConfigDir().resolve(definition.name() + ".json");
  }

  @Override
  public T deserialize() throws SerializationException {
    Path legacyConfigPath = getLegacyConfigPath();

    if (Files.exists(legacyConfigPath)) {
      ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME
          + "] Found legacy configuration file, attempting to load...");
      try {
        File file = legacyConfigPath.toFile();
        T config = jankson.fromJson(jankson.load(file), configClass);

        file.delete();
        ShulkerBoxTooltip.LOGGER
            .info("[" + ShulkerBoxTooltip.MOD_NAME + "] Loaded legacy configuration file!");
        return config;
      } catch (IOException | SyntaxError e) {
        ShulkerBoxTooltip.LOGGER.error(
            "[" + ShulkerBoxTooltip.MOD_NAME + "] Could not load legacy configuration file", e);
      }
    }
    return super.deserialize();
  }

  @Override
  public void serialize(T config) throws SerializationException {
    super.serialize(config);
    Configuration.afterSave();
  }
}
