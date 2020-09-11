package com.misterpemodder.shulkerboxtooltip.impl.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.util.DefaultedTranslatableText;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.GuiRegistry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import me.sargunvohra.mcmods.autoconfig1u.util.Utils;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry.Translatable;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;

@Config(name = "shulkerboxtooltip")
@Config.Gui.Background("minecraft:textures/block/purpur_block.png")
public class Configuration implements ConfigData {
  @ConfigEntry.Category("main")
  @ConfigEntry.Gui.TransitiveObject
  public MainCategory main = new MainCategory();

  @ConfigEntry.Category("controls")
  @ConfigEntry.Gui.TransitiveObject
  public ControlsCategory controls = new ControlsCategory();

  @ConfigEntry.Category("server")
  @ConfigEntry.Gui.TransitiveObject
  public ServerCatergory server = new ServerCatergory();

  public static Configuration register() {
    Configuration configuration = AutoConfig.register(Configuration.class, ShulkerBoxTooltipConfigSerializer::new)
        .getConfig();

    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
      registerGui();
    return configuration;
  }

  @Environment(EnvType.CLIENT)
  @SuppressWarnings("unchecked")
  private static void registerGui() {
    GuiRegistry registry = AutoConfig.getGuiRegistry(Configuration.class);

    // Auto tooltip handling
    registry
        .registerAnnotationTransformer((guis, i13n, field, config, defaults, guiProvider) -> guis.stream().peek(gui -> {
          if (gui instanceof TooltipListEntry)
            ((TooltipListEntry<Object>) gui).setTooltipSupplier(() -> splitTooltipKey(i13n + ".tooltip"));
        }).collect(Collectors.toList()), AutoTooltip.class);

    // Validators
    registry
        .registerAnnotationTransformer((guis, i13n, field, config, defaults, guiProvider) -> guis.stream().peek(gui -> {
          Function<Object, Optional<Text>> validator = getValidatorFunction(field.getAnnotation(Validator.class));

          gui.setErrorSupplier(() -> validator.apply(gui.getValue()));
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
              newValue -> ((Key) Utils.getUnsafely(field, config, new Key(InputUtil.UNKNOWN_KEY))).set(newValue))
          .build();
      entry.setAllowMouse(false);
      return Collections.singletonList(entry);
    }, field -> field.getType() == Key.class);
  }

  @Override
  public void validatePostLoad() throws ValidationException {
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      if (this.controls.previewKey == null)
        this.controls.previewKey = Key.defaultPreviewKey();
      if (this.controls.fullPreviewKey == null)
        this.controls.fullPreviewKey = Key.defaultFullPreviewKey();
    }
    runValidators(MainCategory.class, this.main, "main");
    runValidators(ControlsCategory.class, this.controls, "controls");
    runValidators(ServerCatergory.class, this.server, "server");
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
          throw new ValidationException("ShulkerBoxTooltip config field " + categoryName + "." + field.getName()
              + " is invalid: " + Language.getInstance().get(errorMsg.get().getString()));
      }
    } catch (ReflectiveOperationException | RuntimeException e) {
      throw new ValidationException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> Function<Object, Optional<Text>> getValidatorFunction(Validator validator) {
    try {
      Constructor<Function<Object, Optional<Text>>> constructor = (Constructor<Function<Object, Optional<Text>>>) validator
          .value().getDeclaredConstructor();

      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Couldn't create config validator", e);
    }
  }

  public static class MainCategory {
    @AutoTooltip
    @Comment("Toggles the shulker box preview")
    public boolean enablePreview = true;

    @AutoTooltip
    @Comment("Locks the preview window above the tooltip.\nWhen locked, the window will not adapt when out of screen.")
    public boolean lockPreview = false;

    @AutoTooltip
    @Comment("Swaps the preview modes.\nIf true, pressing the preview key will show the full preview instead.")
    public boolean swapModes = false;

    @AutoTooltip
    @Comment("If on, the preview is always displayed, regardless of the preview key being pressed.")
    public boolean alwaysOn = false;

    @AutoTooltip
    @Comment("Controls whether the key hints in the container's tooltip should be displayed.")
    public boolean showKeyHints = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("The tooltip to use.\nVANILLA: The vanilla tooltip (shows the first 5 items)\nMOD: The mod's tooltip\nNONE: No tooltip")
    public ShulkerBoxTooltipType tooltipType = ShulkerBoxTooltipType.MOD;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("In compact mode, how should items with the same ID but different NBT data be compacted?\nIGNORE: Ignores NBT data\nFIRST_ITEM: Items are displayed as all having the same NBT as the first item\nSEPARATE: Separates items with different NBT data")
    public CompactPreviewTagBehavior compactPreviewTagBehavior = CompactPreviewTagBehavior.SEPARATE;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("Shows info about the current loot table of the item if present.\nVisible only when Tooltip Type is set to Modded.\nHIDE: No loot table info, default.\nSIMPLE: Displays whether the stack uses a loot table.\nADVANCED: Shows the loot table used by the item.")
    public LootTableInfoType lootTableInfoType = LootTableInfoType.HIDE;

    @AutoTooltip
    @Comment("Controls whether the preview window should be colored.")
    public boolean coloredPreview = true;

    @AutoTooltip
    @Validator(GreaterThanZero.class)
    @Comment("The max number of items in a row.\nMay not affect modded containers.")
    public int defaultMaxRowSize = 9;

    @AutoTooltip
    @ConfigEntry.Gui.RequiresRestart
    @Comment("If on, the client will try to send packets to servers to allow extra preview information such as ender chest previews.")
    public boolean serverIntegration = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @Comment("The theme to use.\nAUTO: uses the dark mode setting from LibGui if present, defaults to light theme.\nLIGHT: the regular vanilla-style theme\nDARK: preview windows will be gray instead of white.")
    public Theme theme = Theme.AUTO;
  }

  public static enum ShulkerBoxTooltipType implements Translatable {
    VANILLA, MOD, NONE;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.tooltipType." + this.name().toLowerCase();
    }
  }

  public static enum CompactPreviewTagBehavior implements Translatable {
    IGNORE, FIRST_ITEM, SEPARATE;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.compactPreviewTagBehavior." + this.name().toLowerCase();
    }
  }

  public static enum LootTableInfoType implements Translatable {
    HIDE, SIMPLE, ADVANCED;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.lootTableInfoType." + this.name().toLowerCase();
    }
  }

  public static enum Theme implements Translatable {
    AUTO, LIGHT, DARK;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.theme." + this.name().toLowerCase();
    }
  }

  public static class ControlsCategory {
    @AutoTooltip
    @Comment("Press this key when hovering a container stack to open the preview window.")
    public Key previewKey = Key.defaultPreviewKey();

    @AutoTooltip
    @Comment("Press this key when hovering a container stack to open the full preview window.")
    public Key fullPreviewKey = Key.defaultFullPreviewKey();
  }

  public static class ServerCatergory {
    @AutoTooltip
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.RequiresRestart
    @Comment("If on, the server will be able to provide extra information about containers to the clients with the mod installed.\nDisabling this option will disable all of the options below.")
    public boolean clientIntegration = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.RequiresRestart
    @Comment("Changes the way the ender chest content preview is synchronized.\nNONE: No synchronization, prevents clients from seeing a preview of their ender chest.\nACTIVE: Ender chest contents are synchronized when changed.\nPASSIVE: Ender chest contents are synchonized when the client opens a preview.")
    public EnderChestSyncType enderChestSyncType = EnderChestSyncType.ACTIVE;
  }

  public static enum EnderChestSyncType implements Translatable {
    NONE, ACTIVE, PASSIVE;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.enderChestSyncType." + this.name().toLowerCase();
    }
  }

  private static class GreaterThanZero implements Function<Object, Optional<Text>> {
    @Override
    public Optional<Text> apply(Object value) {
      Class<?> valueClass = value.getClass();
      if (valueClass.equals(Integer.class) && (Integer) value <= 0) {
        return Optional.of(new DefaultedTranslatableText("shulkerboxtooltip.config.validator.greater_than_zero",
            "Must be greater than zero"));
      }
      return Optional.empty();
    }
  }

  @Environment(EnvType.CLIENT)
  public void reinitClientSideSyncedValues() {
    ShulkerBoxTooltip.synchronisedWithServer = false;
    server.clientIntegration = false;
    server.enderChestSyncType = EnderChestSyncType.NONE;
  }

  public void readFromPacketBuf(PacketByteBuf buf) {
    CompoundTag compound = buf.readCompoundTag();

    ShulkerBoxTooltip.synchronisedWithServer = true;
    if (compound.contains("server", NbtType.COMPOUND)) {
      CompoundTag serverTag = compound.getCompound("server");

      if (serverTag.contains("clientIntegration", NbtType.BYTE))
        server.clientIntegration = serverTag.getBoolean("clientIntegration");
      if (serverTag.contains("enderChestSyncType", NbtType.STRING))
        server.enderChestSyncType = getEnumFromName(EnderChestSyncType.class, EnderChestSyncType.NONE,
            serverTag.getString("enderChestSyncType"));
    }
  }

  public void writeToPacketBuf(PacketByteBuf buf) {
    CompoundTag compound = new CompoundTag();
    CompoundTag serverTag = new CompoundTag();

    serverTag.putBoolean("clientIntegration", server.clientIntegration);
    serverTag.putString("enderChestSyncType", server.enderChestSyncType.name());
    compound.put("server", serverTag);

    buf.writeCompoundTag(compound);
  }

  private static <E extends Enum<E>> E getEnumFromName(Class<E> clazz, E defaultValue, String name) {
    if (clazz != null && name != null) {
      try {
        E e = Enum.valueOf(clazz, name);
        return e == null ? defaultValue : e;
      } catch (IllegalArgumentException e) {
      }
    }
    return defaultValue;
  }

  private static Optional<Text[]> splitTooltipKey(String key) {
    String[] lines = Language.getInstance().get(key).split("\n");
    Text[] tooltip = new Text[lines.length];

    for (int i = 0, l = lines.length; i < l; ++i)
      tooltip[i] = new LiteralText(lines[i]);
    return Optional.of(tooltip);
  }
}
