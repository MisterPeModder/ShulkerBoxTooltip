package com.misterpemodder.shulkerboxtooltip.impl.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.Marshaller;
import blue.endless.jankson.api.SyntaxError;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.PluginManager;
import com.misterpemodder.shulkerboxtooltip.impl.color.ColorRegistryImpl;
import com.misterpemodder.shulkerboxtooltip.impl.util.EnvironmentUtil;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ShulkerBoxTooltipConfigSerializer {
  private final Jankson jankson;

  private static final String CONFIG_FILE_NAME = "shulkerboxtooltip.json5";

  public ShulkerBoxTooltipConfigSerializer() {
    this.jankson = this.buildJankson();
  }

  private Jankson buildJankson() {
    Jankson.Builder builder = Jankson.builder();

    if (EnvironmentUtil.isClient())
      ClientOnly.buildJankson(builder);
    return builder.build();
  }

  public void serialize(Configuration config) throws SerializationException {
    Path configPath = this.getConfigPath();

    ShulkerBoxTooltip.LOGGER.debug("Saving configuration to " + configPath);
    try {
      Files.createDirectories(configPath.getParent());
    } catch (IOException e) {
      // attempt to write the config file anyway
    }

    // do not save the config to disk if it is not fully loaded.
    if (EnvironmentUtil.isClient() && !PluginManager.areColorsLoaded()) {
      ShulkerBoxTooltip.LOGGER.debug("Configuration is not fully loaded, not saving");
      return;
    }

    try {
      BufferedWriter writer = Files.newBufferedWriter(configPath);

      writer.write(jankson.toJson(config).toJson(true, true));
      writer.close();
      ShulkerBoxTooltip.LOGGER.debug("Configuration saved successfully");
    } catch (IOException e) {
      throw new SerializationException(e);
    }
  }

  public Configuration deserialize() throws SerializationException {
    Path configPath = this.getConfigPath();

    if (Files.exists(configPath)) {
      try {
        var obj = this.jankson.load(configPath.toFile());
        var config = this.jankson.fromJson(obj, EnvironmentUtil.getInstance().getConfigurationClass());
        if (config == null)
          throw new SerializationException("Failed to deserialize configuration");
        return config;
      } catch (IOException | SyntaxError e) {
        throw new SerializationException(e);
      }
    }
    ShulkerBoxTooltip.LOGGER.info("Could not find configuration file, creating default file");
    return EnvironmentUtil.getInstance().makeConfiguration();
  }

  private Path getConfigPath() {
    return ShulkerBoxTooltip.getConfigDir().resolve(CONFIG_FILE_NAME);
  }

  @Environment(EnvType.CLIENT)
  private static final class ClientOnly {
    private static void buildJankson(Jankson.Builder builder) {
      builder.registerDeserializer(String.class, Key.class, (str, marshaller) -> Key.fromTranslationKey(str));

      builder.registerDeserializer(JsonObject.class, Key.class,
          (obj, marshaller) -> Key.fromTranslationKey(obj.get(String.class, "code")));
      builder.registerSerializer(Key.class, (key, marshaller) -> {
        JsonObject object = new JsonObject();
        object.put("code", new JsonPrimitive(key.get().getName()));
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
