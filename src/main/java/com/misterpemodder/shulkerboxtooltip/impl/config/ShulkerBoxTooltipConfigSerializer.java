package com.misterpemodder.shulkerboxtooltip.impl.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.ControlsCategory;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.PreviewCategory;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.ServerCategory;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.TooltipCategory;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonNull;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonPrimitive;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.DeserializationException;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.Marshaller;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.SyntaxError;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Modified version of JanksonConfigSerializer from AutoConfig
 */
public class ShulkerBoxTooltipConfigSerializer implements ConfigSerializer<Configuration> {
  private Config definition;
  private Jankson jankson;

  public ShulkerBoxTooltipConfigSerializer(Config definition, Class<?> configClass) {
    this(definition, configClass, buildJankson());
  }

  protected ShulkerBoxTooltipConfigSerializer(Config definition, Class<?> configClass,
      Jankson jankson) {
    this.definition = definition;
    this.jankson = jankson;
  }

  private static Jankson buildJankson() {
    Jankson.Builder builder = Jankson.builder();

    builder.registerDeserializer(JsonObject.class, Configuration.class,
        ShulkerBoxTooltipConfigSerializer::fromJson);
    builder.registerSerializer(Configuration.class, ShulkerBoxTooltipConfigSerializer::toJson);
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
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
    }
    return builder.build();
  }

  private static Configuration fromJson(JsonObject obj, Marshaller marshaller) {
    var cfg = new Configuration();
    var previewCategory = marshaller.marshall(PreviewCategory.class, obj.getObject("preview"));
    var tooltipCategory = marshaller.marshall(TooltipCategory.class, obj.getObject("tooltip"));
    var serverCategory = marshaller.marshall(ServerCategory.class, obj.getObject("server"));

    if (previewCategory != null)
      cfg.preview = previewCategory;
    if (tooltipCategory != null)
      cfg.tooltip = tooltipCategory;
    if (serverCategory != null)
      cfg.server = serverCategory;
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      var controlsCategory = marshaller.marshall(ControlsCategory.class, obj.getObject("controls"));

      if (controlsCategory != null)
        cfg.controls = controlsCategory;
    }
    return cfg;
  }

  private static JsonObject toJson(Configuration cfg, Marshaller marshaller) {
    var obj = new JsonObject();

    obj.put("preview", marshaller.serialize(cfg.preview));
    obj.put("tooltip", marshaller.serialize(cfg.tooltip));
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
      obj.put("controls", marshaller.serialize(cfg.controls));
    obj.put("server", marshaller.serialize(cfg.server));
    return obj;
  }

  @Override
  public void serialize(Configuration config) throws SerializationException {
    Path configPath = getConfigPath();
    try {
      Files.createDirectories(configPath.getParent());
    } catch (IOException e) {
    }
    try {
      BufferedWriter writer = Files.newBufferedWriter(configPath);

      writer.write(jankson.toJson(config).toJson(true, true));
      writer.close();
    } catch (IOException e) {
      throw new SerializationException(e);
    }
  }

  private Path getLegacyConfigPath() {
    return FabricLoader.getInstance().getConfigDir().resolve(definition.name() + ".json");
  }

  private Configuration deserializeLegacy() throws SerializationException {
    Path legacyConfigPath = getLegacyConfigPath();

    if (Files.exists(legacyConfigPath)) {
      ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME
          + "] Found legacy configuration file, attempting to load...");
      try {
        File file = legacyConfigPath.toFile();
        Configuration config = this.jankson.fromJson(this.jankson.load(file), Configuration.class);

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
  public Configuration deserialize() throws SerializationException {
    Configuration config = deserializeLegacy();

    if (config != null)
      return config;
    Path configPath = getConfigPath();

    if (Files.exists(configPath)) {
      try {
        var obj = this.jankson.load(configPath.toFile());
        var cfg = this.jankson.fromJsonCarefully(obj, Configuration.class);
        return cfg;
      } catch (IOException | SyntaxError | DeserializationException e) {
        throw new SerializationException(e);
      }
    }
    ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME
        + "] Could not find configuration file, creating default file");
    return createDefault();
  }

  @Override
  public Configuration createDefault() {
    return new Configuration();
  }
}
