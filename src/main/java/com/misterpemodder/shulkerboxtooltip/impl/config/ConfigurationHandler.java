package com.misterpemodder.shulkerboxtooltip.impl.config;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.ControlsCategory;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.EnderChestSyncType;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.PreviewCategory;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.ServerCategory;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.TooltipCategory;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.AutoTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.Validator;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData.ValidationException;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Language;

public final class ConfigurationHandler {
  /**
   * Creates of copy of the passed config object
   * 
   * @param source The source object.
   * @return The newly created object.
   */
  public static Configuration copyOf(Configuration source) {
    Configuration c = new Configuration();

    c.preview = Configuration.PreviewCategory.copyFrom(source.preview);
    c.tooltip = Configuration.TooltipCategory.copyFrom(source.tooltip);
    c.server = Configuration.ServerCategory.copyFrom(source.server);
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
      c.controls = Configuration.ControlsCategory.copyFrom(source.controls);
    return c;
  }

  public static Configuration register() {
    Configuration configuration = AutoConfig
        .register(Configuration.class, ShulkerBoxTooltipConfigSerializer::new).getConfig();

    AutoConfig.getConfigHolder(Configuration.class).registerSaveListener((holder, config) -> {
      onSave();
      return ActionResult.PASS;
    });
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
      ConfigurationHandler.registerGui();
    return configuration;
  }

  @Environment(EnvType.CLIENT)
  private static void registerGui() {
    GuiRegistry registry = AutoConfig.getGuiRegistry(Configuration.class);

    // Auto tooltip handling
    registry.registerAnnotationTransformer(
        (guis, i13n, field, config, defaults, guiProvider) -> guis.stream().peek(gui -> {
          if (gui instanceof TooltipListEntry<?> entry)
            entry.setTooltipSupplier(() -> splitTooltipKey(i13n + ".tooltip"));
        }).collect(Collectors.toList()), AutoTooltip.class);

    // Validators
    registry.registerAnnotationTransformer(
        (guis, i13n, field, config, defaults, guiProvider) -> guis.stream().peek(gui -> {
          var validator = getValidatorFunction(field.getAnnotation(Validator.class));

          ((AbstractConfigListEntry<?>) gui)
              .setErrorSupplier(() -> validator.apply(gui.getValue()));
        }).collect(Collectors.toList()), Validator.class);

    // Keybind UI
    registry.registerPredicateProvider((i13n, field, config, defaults, guiProvider) -> {
      if (field.isAnnotationPresent(ConfigEntry.Gui.Excluded.class))
        return Collections.emptyList();
      KeyCodeEntry entry = ConfigEntryBuilder.create()
          .startKeyCodeField(new TranslatableText(i13n),
              Utils.getUnsafely(field, config, new Key(InputUtil.UNKNOWN_KEY)).get())
          .setDefaultValue(() -> ((Key) Utils.getUnsafely(field, defaults)).get())
          .setSaveConsumer(
              newValue -> ((Key) Utils.getUnsafely(field, config, new Key(InputUtil.UNKNOWN_KEY)))
                  .set(newValue))
          .build();

      entry.setAllowMouse(false);
      return Collections.singletonList(entry);
    }, field -> field.getType() == Key.class);
  }

  private static Optional<Text[]> splitTooltipKey(String key) {
    String[] lines = Language.getInstance().get(key).split("\n");
    Text[] tooltip = new Text[lines.length];

    for (int i = 0, l = lines.length; i < l; ++i)
      tooltip[i] = new LiteralText(lines[i]);
    return Optional.of(tooltip);
  }

  public static void validate(Configuration config) throws ValidationException {
    runValidators(PreviewCategory.class, config.preview, "preview");
    runValidators(TooltipCategory.class, config.tooltip, "tooltip");
    runValidators(ServerCategory.class, config.server, "server");
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      if (config.controls.previewKey == null)
        config.controls.previewKey = Key.defaultPreviewKey();
      if (config.controls.fullPreviewKey == null)
        config.controls.fullPreviewKey = Key.defaultFullPreviewKey();
      runValidators(ControlsCategory.class, config.controls, "controls");
    }
  }

  private static <T> void runValidators(Class<T> categoryClass, T category, String categoryName)
      throws ValidationException {
    try {
      for (Field field : categoryClass.getDeclaredFields()) {
        Validator validator = field.getAnnotation(Validator.class);

        if (validator == null)
          continue;
        field.setAccessible(true);

        Optional<Text> errorMsg = getValidatorFunction(validator).apply(field.get(category));

        if (errorMsg.isPresent())
          throw new ValidationException(
              "ShulkerBoxTooltip config field " + categoryName + "." + field.getName()
                  + " is invalid: " + Language.getInstance().get(errorMsg.get().getString()));
      }
    } catch (ReflectiveOperationException | RuntimeException e) {
      throw new ValidationException(e);
    }
  }

  private static <T> Function<Object, Optional<Text>> getValidatorFunction(Validator validator) {
    try {
      var constructor = validator.value().getDeclaredConstructor();

      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Couldn't create config validator", e);
    }
  }

  @Environment(EnvType.CLIENT)
  public static void reinitClientSideSyncedValues(Configuration config) {
    ShulkerBoxTooltip.synchronisedWithServer = false;
    config.server.clientIntegration = false;
    config.server.enderChestSyncType = EnderChestSyncType.NONE;
  }

  public static void onSave() {
    if (ShulkerBoxTooltip.savedConfig == null)
      return;

    ServerCategory serverCategory =
        ShulkerBoxTooltip.config == null ? new ServerCategory() : ShulkerBoxTooltip.config.server;

    ShulkerBoxTooltip.config = ConfigurationHandler.copyOf(ShulkerBoxTooltip.savedConfig);
    ShulkerBoxTooltip.config.server = serverCategory;
  }

  public static void readFromPacketBuf(Configuration config, PacketByteBuf buf) {
    NbtCompound compound = buf.readNbt();

    ShulkerBoxTooltip.synchronisedWithServer = true;
    if (compound.contains("server", NbtType.COMPOUND)) {
      NbtCompound serverTag = compound.getCompound("server");

      if (serverTag.contains("clientIntegration", NbtType.BYTE))
        config.server.clientIntegration = serverTag.getBoolean("clientIntegration");
      if (serverTag.contains("enderChestSyncType", NbtType.STRING))
        config.server.enderChestSyncType = getEnumFromName(EnderChestSyncType.class,
            EnderChestSyncType.NONE, serverTag.getString("enderChestSyncType"));
    }
  }

  public static void writeToPacketBuf(Configuration config, PacketByteBuf buf) {
    NbtCompound compound = new NbtCompound();
    NbtCompound serverTag = new NbtCompound();

    serverTag.putBoolean("clientIntegration", config.server.clientIntegration);
    serverTag.putString("enderChestSyncType", config.server.enderChestSyncType.name());
    compound.put("server", serverTag);

    buf.writeNbt(compound);
  }

  private static <E extends Enum<E>> E getEnumFromName(Class<E> clazz, E defaultValue,
      String name) {
    if (clazz != null && name != null) {
      try {
        E e = Enum.valueOf(clazz, name);
        return e == null ? defaultValue : e;
      } catch (IllegalArgumentException e) {
      }
    }
    return defaultValue;
  }
}
