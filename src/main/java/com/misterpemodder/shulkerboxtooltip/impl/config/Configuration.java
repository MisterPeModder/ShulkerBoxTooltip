package com.misterpemodder.shulkerboxtooltip.impl.config;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.gui.registry.GuiRegistry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry.Translatable;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.util.Language;

@Config(name = "shulkerboxtooltip")
@Config.Gui.Background("minecraft:textures/block/purpur_block.png")
public class Configuration implements ConfigData {
  @ConfigEntry.Category("main")
  @ConfigEntry.Gui.TransitiveObject
  public MainCategory main = new MainCategory();

  @SuppressWarnings("unchecked")
  public static Configuration register() {
    AutoConfig.register(Configuration.class, GsonConfigSerializer::new);
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
    return AutoConfig.getConfigHolder(Configuration.class).getConfig();
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
    public ShulkerBoxTooltipType tooltipType = ShulkerBoxTooltipType.MOD;
    @AutoTooltip
    public CompactPreviewTagBehavior compactPreviewTagBehavior = CompactPreviewTagBehavior.SEPARATE;
    @AutoTooltip
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
      return "shulkerboxtooltip.tooltipType." + name().toLowerCase();
    }
  }

  public static enum CompactPreviewTagBehavior implements Translatable {
    IGNORE, FIRST_ITEM, SEPARATE;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.compactPreviewTagBehavior." + name().toLowerCase();
    }
  }

  public static enum LootTableInfoType implements Translatable {
    HIDE, SIMPLE, ADVANCED;

    @Override
    public String getKey() {
      return "shulkerboxtooltip.lootTableInfoType." + name().toLowerCase();
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
}
