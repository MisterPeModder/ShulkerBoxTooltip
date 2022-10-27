package com.misterpemodder.shulkerboxtooltip.impl.config;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.PluginManager;
import com.misterpemodder.shulkerboxtooltip.impl.color.ColorRegistryImpl;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.*;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonPrimitive;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.DeserializationException;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.Marshaller;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.SyntaxError;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Modified version of JanksonConfigSerializer from AutoConfig
 */
public class ShulkerBoxTooltipConfigSerializer implements ConfigSerializer<Configuration> {
  private final Config definition;
  private final Jankson jankson;

  public ShulkerBoxTooltipConfigSerializer(Config definition, Class<?> configClass) {
    this(definition, configClass, buildJankson());
  }

  @SuppressWarnings("unused")
  protected ShulkerBoxTooltipConfigSerializer(Config definition, Class<?> configClass, Jankson jankson) {
    this.definition = definition;
    this.jankson = jankson;
  }

  private static Jankson buildJankson() {
    Jankson.Builder builder = Jankson.builder();

    builder.registerDeserializer(JsonObject.class, Configuration.class, ShulkerBoxTooltipConfigSerializer::fromJson);
    builder.registerSerializer(Configuration.class, ShulkerBoxTooltipConfigSerializer::toJson);
    if (ShulkerBoxTooltip.isClient())
      ClientOnly.buildJankson(builder);
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
    if (ShulkerBoxTooltip.isClient()) {
      var colorsCategory = marshaller.marshall(ColorsCategory.class, obj.getObject("colors"));
      var controlsCategory = marshaller.marshall(ControlsCategory.class, obj.getObject("controls"));

      if (colorsCategory != null)
        cfg.colors = colorsCategory;
      if (controlsCategory != null)
        cfg.controls = controlsCategory;
    }
    return cfg;
  }

  private static JsonObject toJson(Configuration cfg, Marshaller marshaller) {
    var obj = new JsonObject();

    obj.put("preview", marshaller.serialize(cfg.preview));
    obj.put("tooltip", marshaller.serialize(cfg.tooltip));
    if (ShulkerBoxTooltip.isClient()) {
      obj.put("colors", marshaller.serialize(cfg.colors));
      obj.put("controls", marshaller.serialize(cfg.controls));
    }
    obj.put("server", marshaller.serialize(cfg.server));
    return obj;
  }

  @Override
  public void serialize(Configuration config) throws SerializationException {
    Path configPath = getConfigPath();

    ShulkerBoxTooltip.LOGGER.debug('[' + ShulkerBoxTooltip.MOD_NAME + "] Saving configuration to " + configPath);
    try {
      Files.createDirectories(configPath.getParent());
    } catch (IOException e) {
      // attempt to write the config file anyway
    }

    // do not save the config to disk if it is not fully loaded.
    if (ShulkerBoxTooltip.isClient() && !PluginManager.areColorsLoaded()) {
      ShulkerBoxTooltip.LOGGER.debug(
          '[' + ShulkerBoxTooltip.MOD_NAME + "] Configuration is not fully loaded, not saving");
      return;
    }

    try {
      BufferedWriter writer = Files.newBufferedWriter(configPath);

      writer.write(jankson.toJson(config).toJson(true, true));
      writer.close();
      ShulkerBoxTooltip.LOGGER.debug('[' + ShulkerBoxTooltip.MOD_NAME + "] Configuration saved successfully");
    } catch (IOException e) {
      throw new SerializationException(e);
    }
  }

  private Path getLegacyConfigPath() {
    return ShulkerBoxTooltip.getConfigDir().resolve(definition.name() + ".json");
  }

  // TODO: remove for Minecraft 1.20
  @Deprecated(forRemoval = true)
  @ApiStatus.ScheduledForRemoval(inVersion = "4.0.0")
  private Configuration deserializeLegacy() {
    Path legacyConfigPath = getLegacyConfigPath();

    if (Files.exists(legacyConfigPath)) {
      ShulkerBoxTooltip.LOGGER.info(
          "[" + ShulkerBoxTooltip.MOD_NAME + "] Found legacy configuration file, attempting to load...");
      try {
        File file = legacyConfigPath.toFile();
        Configuration config = this.jankson.fromJson(this.jankson.load(file), Configuration.class);

        //noinspection ResultOfMethodCallIgnored
        file.delete();
        ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME + "] Loaded legacy configuration file!");
        return config;
      } catch (IOException | SyntaxError e) {
        ShulkerBoxTooltip.LOGGER.error("[" + ShulkerBoxTooltip.MOD_NAME + "] Could not load legacy configuration file",
            e);
      }
    }
    return null;
  }

  private Path getConfigPath() {
    return ShulkerBoxTooltip.getConfigDir().resolve(definition.name() + ".json5");
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
        return this.jankson.fromJsonCarefully(obj, Configuration.class);
      } catch (IOException | SyntaxError | DeserializationException e) {
        throw new SerializationException(e);
      }
    }
    ShulkerBoxTooltip.LOGGER.info(
        "[" + ShulkerBoxTooltip.MOD_NAME + "] Could not find configuration file, creating default file");
    return createDefault();
  }

  @Override
  public Configuration createDefault() {
    return new Configuration();
  }

  @Environment(EnvType.CLIENT)
  private static final class ClientOnly {
    private static void buildJankson(Jankson.Builder builder) {
      builder.registerDeserializer(String.class, Key.class, (str, marshaller) -> Key.fromTranslationKey(str));

      builder.registerDeserializer(JsonObject.class, Key.class,
          (obj, marshaller) -> Key.fromTranslationKey(obj.get(String.class, "code")));
      builder.registerSerializer(Key.class, (key, marshaller) -> {
        JsonObject object = new JsonObject();
        object.put("code", new JsonPrimitive(key.get().getTranslationKey()));
        return object;
      });

      builder.registerDeserializer(JsonObject.class, ColorRegistry.class, ClientOnly::deserializeColorRegistry);
      builder.registerSerializer(ColorRegistry.class, ClientOnly::serializeColorRegistry);
    }

    private static ColorRegistry deserializeColorRegistry(JsonObject obj, Marshaller marshaller) {
      for (var categoryEntry : obj.entrySet()) {
        var categoryId = Identifier.tryParse(categoryEntry.getKey());

        if (categoryId != null && categoryEntry.getValue() instanceof JsonObject categoryObject)
          deserializeColorCategory(categoryId, categoryObject);
      }
      return ColorRegistryImpl.INSTANCE;
    }

    private static JsonObject serializeColorRegistry(ColorRegistry registry, Marshaller marshaller) {
      JsonObject object = new JsonObject();

      for (var categoryEntry : registry.categories().entrySet()) {
        JsonObject categoryObject = new JsonObject();

        for (var keyEntry : categoryEntry.getValue().keys().entrySet()) {
          categoryObject.put(keyEntry.getKey(), new JsonHexadecimalInt(keyEntry.getValue().rgb()));
          categoryObject.setComment(keyEntry.getKey(),
              String.format("(default value: %#x)", keyEntry.getValue().defaultRgb()));
        }
        object.put(categoryEntry.getKey().toString(), categoryObject);
      }
      return object;
    }

    private static void deserializeColorCategory(Identifier id, JsonObject object) {
      var category = ColorRegistryImpl.INSTANCE.category(id);

      for (var entry : object.entrySet()) {
        if (entry.getValue() instanceof JsonPrimitive value) {
          ColorKey key = category.key(entry.getKey());

          long rgbValue = value.asLong(Long.MIN_VALUE);
          boolean isValidValue = rgbValue >= Integer.MIN_VALUE && rgbValue <= Integer.MAX_VALUE;

          if (key != null) {
            if (isValidValue)
              key.setRgb((int) rgbValue);
            else
              // reset to default if the value is invalid
              key.setRgb(key.defaultRgb());
          } else if (isValidValue) {
            // key is not (yet) registered, save this value in case it gets registered later
            category.setRgbKeyLater(entry.getKey(), (int) rgbValue);
          }
        }
      }
    }
  }
}
