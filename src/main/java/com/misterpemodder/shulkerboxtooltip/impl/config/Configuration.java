package com.misterpemodder.shulkerboxtooltip.impl.config;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.GuiRegistry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry.Translatable;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Language;

@Config(name = "shulkerboxtooltip")
@Config.Gui.Background("minecraft:textures/block/purpur_block.png")
public class Configuration implements ConfigData {
  @ConfigEntry.Category("main")
  @ConfigEntry.Gui.TransitiveObject
  public MainCategory main = new MainCategory();

  @ConfigEntry.Category("server")
  @ConfigEntry.Gui.TransitiveObject
  public ServerCatergory server = new ServerCatergory();

  public static Configuration register() {
    Configuration configuration =
        AutoConfig.register(Configuration.class, GsonConfigSerializer::new).getConfig();

    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
      registerGui();
    return configuration;
  }

  @Environment(EnvType.CLIENT)
  @SuppressWarnings("unchecked")
  private static void registerGui() {
    GuiRegistry registry = AutoConfig.getGuiRegistry(Configuration.class);

    registry.registerAnnotationTransformer(
        (guis, i13n, field, config, defaults, guiProvider) -> guis.stream().peek(gui -> {
          if (gui instanceof TooltipListEntry) {
            String key = i13n + ".tooltip";

            ((TooltipListEntry<Object>) gui).setTooltipSupplier(
                () -> Optional.of(Language.getInstance().translate(key).split("\n")));
          }
        }).collect(Collectors.toList()), AutoTooltip.class);
    registry.registerAnnotationTransformer(
        (guis, i13n, field, config, defaults, guiProvider) -> guis.stream().peek(gui -> {
          try {
            Constructor<Function<Object, Optional<String>>> constructor =
                (Constructor<Function<Object, Optional<String>>>) field
                    .getAnnotation(Validator.class).value().getDeclaredConstructor();

            constructor.setAccessible(true);

            Function<Object, Optional<String>> validator = constructor.newInstance();

            gui.setErrorSupplier(() -> validator.apply(gui.getValue()));
          } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Couldn't create config validator", e);
          }
        }).collect(Collectors.toList()), Validator.class);
  }

  public static class MainCategory {
    @AutoTooltip
    public boolean enablePreview = true;

    @AutoTooltip
    public boolean lockPreview = false;

    @AutoTooltip
    public boolean swapModes = false;

    @AutoTooltip
    public boolean alwaysOn = false;

    @AutoTooltip
    public boolean showKeyHints = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    public ShulkerBoxTooltipType tooltipType = ShulkerBoxTooltipType.MOD;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    public CompactPreviewTagBehavior compactPreviewTagBehavior = CompactPreviewTagBehavior.SEPARATE;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    public LootTableInfoType lootTableInfoType = LootTableInfoType.HIDE;

    @AutoTooltip
    public boolean coloredPreview = true;

    @AutoTooltip
    @Validator(GreaterThanZero.class)
    public int defaultMaxRowSize = 9;
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

  public static class ServerCatergory {
    @AutoTooltip
    @ConfigEntry.Gui.PrefixText
    public boolean clientIntegration = true;

    @AutoTooltip
    @ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
    public EnderChestSyncType enderChestSyncType = EnderChestSyncType.ACTIVE;
  }

  public static enum EnderChestSyncType implements Translatable {
    NONE, ACTIVE, PASSIVE;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.enderChestSyncType." + this.name().toLowerCase();
    }
  }

  private static class GreaterThanZero implements Function<Object, Optional<String>> {
    @Override
    public Optional<String> apply(Object value) {
      Class<?> valueClass = value.getClass();
      if (valueClass.equals(Integer.class) && (Integer) value <= 0) {
        return Optional.of(Language.getInstance()
            .translate("shulkerboxtooltip.config.validator.greater_than_zero"));
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
        server.enderChestSyncType = getEnumFromName(EnderChestSyncType.class,
            EnderChestSyncType.NONE, serverTag.getString("enderChestSyncType"));
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
