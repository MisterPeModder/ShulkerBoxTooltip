package com.misterpemodder.shulkerboxtooltip;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.provider.BlockEntityAwarePreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.provider.EnderChestPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.provider.ShulkerBoxPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.util.NamedLogger;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.*;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Supplier;

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

  public static void init() {
    savedConfig = ConfigurationHandler.register();
    config = ConfigurationHandler.copyOf(savedConfig);
    ServerNetworking.init();
  }

  private static void register(PreviewProviderRegistry registry, String id, PreviewProvider provider, Item... items) {
    registry.register(ShulkerBoxTooltipUtil.id(id), provider, items);
  }

  private static <BE extends BlockEntity & Inventory> void register(PreviewProviderRegistry registry, String id,
      int maxRowSize, BiFunction<Integer, Supplier<BE>, PreviewProvider> providerFactory,
      BiFunction<BlockPos, BlockState, BE> blockEntityFactory, Block... blocks) {
    for (var block : blocks) {
      var provider = providerFactory.apply(maxRowSize,
          () -> blockEntityFactory.apply(BlockPos.ORIGIN, block.getDefaultState()));
      registry.register(ShulkerBoxTooltipUtil.id(id), provider, block.asItem());
    }
  }

  @Override
  public void registerProviders(PreviewProviderRegistry registry) {
    register(registry, "shulker_box", 9, ShulkerBoxPreviewProvider::new, ShulkerBoxBlockEntity::new, Blocks.SHULKER_BOX,
        Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX,
        Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX,
        Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX,
        Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    register(registry, "chest_like", 9, BlockEntityAwarePreviewRenderer::new, ChestBlockEntity::new, Blocks.CHEST,
        Blocks.TRAPPED_CHEST, Blocks.BARREL);
    register(registry, "furnace_like", 3, BlockEntityAwarePreviewRenderer::new, FurnaceBlockEntity::new,
        Blocks.FURNACE);
    register(registry, "furnace_like", 3, BlockEntityAwarePreviewRenderer::new, BlastFurnaceBlockEntity::new,
        Blocks.BLAST_FURNACE);
    register(registry, "furnace_like", 3, BlockEntityAwarePreviewRenderer::new, SmokerBlockEntity::new,
        Blocks.SMOKER);
    register(registry, "dropper_like", 9, BlockEntityAwarePreviewRenderer::new, DropperBlockEntity::new,
        Blocks.DROPPER);
    register(registry, "dropper_like", 9, BlockEntityAwarePreviewRenderer::new, DispenserBlockEntity::new,
        Blocks.DISPENSER);
    register(registry, "hopper", 5, BlockEntityAwarePreviewRenderer::new, BrewingStandBlockEntity::new,
        Blocks.HOPPER);
    register(registry, "brewing_stand", 5, BlockEntityAwarePreviewRenderer::new, BrewingStandBlockEntity::new,
        Blocks.BREWING_STAND);
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
  @Contract(value = "-> _", pure = true)
  public static boolean isClient() {
    //noinspection Contract
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.isClient()");
  }

  /**
   * Get the current directory for game configuration files.
   *
   * @return the configuration directory.
   */
  @ExpectPlatform
  @Contract(value = "-> _", pure = true)
  public static Path getConfigDir() {
    //noinspection Contract
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.getConfigDir()");
  }
}
