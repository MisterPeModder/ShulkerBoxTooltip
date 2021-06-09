package com.misterpemodder.shulkerboxtooltip.impl.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonNull;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonPrimitive;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.SyntaxError;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Modified version of JanksonConfigSerializer from AutoConfig
 */
public class ShulkerBoxTooltipConfigSerializer<T extends ConfigData>
    implements ConfigSerializer<T> {
  private Config definition;
  private Class<T> configClass;
  private Jankson jankson;

  public ShulkerBoxTooltipConfigSerializer(Config definition, Class<T> configClass) {
    this(definition, configClass, buildJankson());
  }

  protected ShulkerBoxTooltipConfigSerializer(Config definition, Class<T> configClass,
      Jankson jankson) {
    this.definition = definition;
    this.configClass = configClass;
    this.jankson = jankson;
  }

  private static Jankson buildJankson() {
    Jankson.Builder builder = Jankson.builder();

    builder.registerDeserializer(String.class, Key.class, (str, marshaller) -> {
      if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
        return null;
      return Key.fromTranslationKey(str);
    });
    builder.registerDeserializer(JsonObject.class, Key.class, (obj, marshaller) -> {
      if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
        return null;
      return Key.fromTranslationKey(obj.get(String.class, "code"));
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

  @Override
  public void serialize(T config) throws SerializationException {
    Path configPath = getConfigPath();
    try {
      Files.createDirectories(configPath.getParent());

      BufferedWriter writer = Files.newBufferedWriter(configPath);

      writer.write(jankson.toJson(config).toJson(true, true));
      writer.close();
      ConfigurationHandler.afterSave();
    } catch (IOException e) {
      throw new SerializationException(e);
    }
  }

  private Path getLegacyConfigPath() {
    return FabricLoader.getInstance().getConfigDir().resolve(definition.name() + ".json");
  }

  private T deserializeLegacy() throws SerializationException {
    Path legacyConfigPath = getLegacyConfigPath();

    if (Files.exists(legacyConfigPath)) {
      ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME
          + "] Found legacy configuration file, attempting to load...");
      try {
        File file = legacyConfigPath.toFile();
        T config = this.jankson.fromJson(this.jankson.load(file), configClass);

        file.delete();
        ShulkerBoxTooltip.LOGGER
            .info("[" + ShulkerBoxTooltip.MOD_NAME + "] Loaded legacy configuration file!");
        return config;
      } catch (IOException | SyntaxError e) {
        ShulkerBoxTooltip.LOGGER.error(
            "[" + ShulkerBoxTooltip.MOD_NAME + "] Could not load legacy configuration file", e);
      }
    }
    return null;
  }

  private Path getConfigPath() {
    return FabricLoader.getInstance().getConfigDir().resolve(definition.name() + ".json5");
  }

  @Override
  public T deserialize() throws SerializationException {
    T config = deserializeLegacy();

    if (config != null)
      return config;
    Path configPath = getConfigPath();

    if (Files.exists(configPath)) {
      try {
        return this.jankson.fromJson(this.jankson.load(configPath.toFile()), this.configClass);
      } catch (IOException | SyntaxError e) {
        throw new SerializationException(e);
      }
    }
    ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME
        + "] Could not find configuration file, creating default file");
    return createDefault();
  }

  @Override
  public T createDefault() {
    try {
      var constructor = this.configClass.getDeclaredConstructor();

      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
