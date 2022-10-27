package com.misterpemodder.shulkerboxtooltip;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.provider.EnderChestPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.provider.ShulkerBoxPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.util.NamedLogger;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;

@ApiStatus.Internal
@ParametersAreNonnullByDefault
public class ShulkerBoxTooltip implements ShulkerBoxTooltipApi {
  public static final String MOD_ID = "shulkerboxtooltip";
  public static final String MOD_NAME = "ShulkerBoxTooltip";
  public static final NamedLogger LOGGER = new NamedLogger(LogManager.getFormatterLogger(MOD_NAME));

  /**
   * The active config object, some of its properties are synced with the server.
   */
  public static Configuration config;
  /**
   * the actual config object, its values are never synced.
   */
  public static Configuration savedConfig;

  /**
   * A list of all the vanilla shulker box items.
   */
  public static final Item[] SHULKER_BOX_ITEMS =
      new Item[] {Items.SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX,
          Items.LIGHT_BLUE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX, Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX,
          Items.GRAY_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
          Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.RED_SHULKER_BOX,
          Items.BLACK_SHULKER_BOX};

  public static void init() {
    savedConfig = ConfigurationHandler.register();
    config = ConfigurationHandler.copyOf(savedConfig);
    ServerNetworking.init();
  }

  private static void register(PreviewProviderRegistry registry, String id, PreviewProvider provider, Item... items) {
    registry.register(ShulkerBoxTooltipUtil.id(id), provider, items);
  }

  @Override
  public void registerProviders(PreviewProviderRegistry registry) {
    register(registry, "shulker_box", new ShulkerBoxPreviewProvider(), SHULKER_BOX_ITEMS);
    register(registry, "chest_like", new BlockEntityPreviewProvider(27, true), Items.CHEST, Items.TRAPPED_CHEST,
        Items.BARREL);
    register(registry, "furnace_like", new BlockEntityPreviewProvider(3, false, 1), Items.FURNACE, Items.BLAST_FURNACE,
        Items.SMOKER);
    register(registry, "dropper_like", new BlockEntityPreviewProvider(9, true, 3), Items.DROPPER, Items.DISPENSER);
    register(registry, "hopper", new BlockEntityPreviewProvider(5, true, 5), Items.HOPPER);
    register(registry, "brewing_stand", new BlockEntityPreviewProvider(5, false, 3), Items.BREWING_STAND);
    register(registry, "ender_chest", new EnderChestPreviewProvider(), Items.ENDER_CHEST);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void registerColors(ColorRegistry registry) {
    // @formatter:off
    registry.defaultCategory()
        .register(ColorKey.DEFAULT, "default")
        .register(ColorKey.ENDER_CHEST, "ender_chest", blockName("ender_chest"));

    registry.category(ShulkerBoxTooltipUtil.id("shulker_boxes"))
        .register(ColorKey.SHULKER_BOX, "shulker_box", blockName("shulker_box"))
        .register(ColorKey.WHITE_SHULKER_BOX, "white_shulker_box", blockName("white_shulker_box"))
        .register(ColorKey.ORANGE_SHULKER_BOX, "orange_shulker_box", blockName("orange_shulker_box"))
        .register(ColorKey.MAGENTA_SHULKER_BOX, "magenta_shulker_box", blockName("magenta_shulker_box"))
        .register(ColorKey.LIGHT_BLUE_SHULKER_BOX, "light_blue_shulker_box", blockName("light_blue_shulker_box"))
        .register(ColorKey.YELLOW_SHULKER_BOX, "yellow_shulker_box", blockName("yellow_shulker_box"))
        .register(ColorKey.LIME_SHULKER_BOX, "lime_shulker_box", blockName("lime_shulker_box"))
        .register(ColorKey.PINK_SHULKER_BOX, "pink_shulker_box", blockName("pink_shulker_box"))
        .register(ColorKey.GRAY_SHULKER_BOX, "gray_shulker_box", blockName("gray_shulker_box"))
        .register(ColorKey.LIGHT_GRAY_SHULKER_BOX, "light_gray_shulker_box", blockName("light_gray_shulker_box"))
        .register(ColorKey.CYAN_SHULKER_BOX, "cyan_shulker_box", blockName("cyan_shulker_box"))
        .register(ColorKey.PURPLE_SHULKER_BOX, "purple_shulker_box", blockName("purple_shulker_box"))
        .register(ColorKey.BLUE_SHULKER_BOX, "blue_shulker_box", blockName("blue_shulker_box"))
        .register(ColorKey.BROWN_SHULKER_BOX, "brown_shulker_box", blockName("brown_shulker_box"))
        .register(ColorKey.GREEN_SHULKER_BOX, "green_shulker_box", blockName("green_shulker_box"))
        .register(ColorKey.RED_SHULKER_BOX, "red_shulker_box", blockName("red_shulker_box"))
        .register(ColorKey.BLACK_SHULKER_BOX, "black_shulker_box", blockName("black_shulker_box"));
    // @formatter:on
  }

  private static String blockName(String block) {
    return "block.minecraft." + block;
  }

  /**
   * @return Whether the current environment type (or Dist in forge terms) is the client.
   */
  @ExpectPlatform
  public static boolean isClient() {
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.isClient()");
  }

  /**
   * Get the current directory for game configuration files.
   *
   * @return the configuration directory.
   */
  @ExpectPlatform
  public static Path getConfigDir() {
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.getConfigDir()");
  }
}
