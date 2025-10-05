package com.misterpemodder.shulkerboxtooltip;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.provider.*;
import com.misterpemodder.shulkerboxtooltip.impl.tree.RootConfigNode;
import com.misterpemodder.shulkerboxtooltip.impl.util.EnvironmentUtil;
import com.misterpemodder.shulkerboxtooltip.impl.util.NamedLogger;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.*;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

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
  public static RootConfigNode<Configuration> configTree;

  public static void init() {
    configTree = RootConfigNode.create(EnvironmentUtil.getInstance().makeConfiguration());
    savedConfig = ConfigurationHandler.register();
    config = EnvironmentUtil.getInstance().makeConfiguration();
    configTree.copy(savedConfig, config);
    ServerNetworking.init();
  }

  @Override
  public void registerProviders(PreviewProviderRegistry registry) {
    // @formatter:off
    new FixedPreviewProviderRegistry<>(registry, ShulkerBoxPreviewProvider::new)
        .register("shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.SHULKER_BOX)
        .register("white_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.WHITE_SHULKER_BOX)
        .register("orange_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.ORANGE_SHULKER_BOX)
        .register("magenta_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.MAGENTA_SHULKER_BOX)
        .register("light_blue_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.LIGHT_BLUE_SHULKER_BOX)
        .register("yellow_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.YELLOW_SHULKER_BOX)
        .register("lime_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.LIME_SHULKER_BOX)
        .register("pink_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.PINK_SHULKER_BOX)
        .register("gray_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.GRAY_SHULKER_BOX)
        .register("light_gray_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.LIGHT_GRAY_SHULKER_BOX)
        .register("cyan_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.CYAN_SHULKER_BOX)
        .register("purple_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.PURPLE_SHULKER_BOX)
        .register("blue_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.BLUE_SHULKER_BOX)
        .register("brown_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.BROWN_SHULKER_BOX)
        .register("green_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.GREEN_SHULKER_BOX)
        .register("red_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.RED_SHULKER_BOX)
        .register("black_shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.BLACK_SHULKER_BOX);

    new FixedPreviewProviderRegistry<>(registry, InventoryAwarePreviewProvider::new)
        .register("chest", 9, ChestBlockEntity::new, Blocks.CHEST)
        .register("copper_chest", 9, ChestBlockEntity::new, Blocks.COPPER_CHEST)
        .register("exposed_copper_chest", 9, ChestBlockEntity::new, Blocks.EXPOSED_COPPER_CHEST)
        .register("weathered_copper_chest", 9, ChestBlockEntity::new, Blocks.WEATHERED_COPPER_CHEST)
        .register("oxidized_copper_chest", 9, ChestBlockEntity::new, Blocks.OXIDIZED_COPPER_CHEST)
        .register("waxed_copper_chest", 9, ChestBlockEntity::new, Blocks.WAXED_COPPER_CHEST)
        .register("waxed_exposed_copper_chest", 9, ChestBlockEntity::new, Blocks.WAXED_EXPOSED_COPPER_CHEST)
        .register("waxed_weathered_copper_chest", 9, ChestBlockEntity::new, Blocks.WAXED_WEATHERED_COPPER_CHEST)
        .register("waxed_oxidized_copper_chest", 9, ChestBlockEntity::new, Blocks.WAXED_OXIDIZED_COPPER_CHEST)
        .register("trapped_chest", 9, TrappedChestBlockEntity::new, Blocks.TRAPPED_CHEST)
        .register("barrel", 9, BarrelBlockEntity::new, Blocks.BARREL)
        .register("furnace", 3, FurnaceBlockEntity::new, Blocks.FURNACE)
        .register("blast_furnace", 3, BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE)
        .register("smoker", 3, SmokerBlockEntity::new, Blocks.SMOKER)
        .register("dropper", 3, DropperBlockEntity::new, Blocks.DROPPER)
        .register("dispenser", 3, DispenserBlockEntity::new, Blocks.DISPENSER)
        .register("hopper", 5, HopperBlockEntity::new, Blocks.HOPPER)
        .register("brewing_stand", 5, BrewingStandBlockEntity::new, Blocks.BREWING_STAND)
        .register("chiseled_bookshelf", 3, ChiseledBookShelfBlockEntity::new, Blocks.CHISELED_BOOKSHELF)
        .register("decorated_pot", 1, DecoratedPotBlockEntity::new, Blocks.DECORATED_POT)
        .register("acacia_shelf", 3, ShelfBlockEntity::new, Blocks.ACACIA_SHELF)
        .register("bamboo_shelf", 3, ShelfBlockEntity::new, Blocks.BAMBOO_SHELF)
        .register("birch_shelf", 3, ShelfBlockEntity::new, Blocks.BIRCH_SHELF)
        .register("cherry_shelf", 3, ShelfBlockEntity::new, Blocks.CHERRY_SHELF)
        .register("crimson_shelf", 3, ShelfBlockEntity::new, Blocks.CRIMSON_SHELF)
        .register("dark_oak_shelf", 3, ShelfBlockEntity::new, Blocks.DARK_OAK_SHELF)
        .register("jungle_shelf", 3, ShelfBlockEntity::new, Blocks.JUNGLE_SHELF)
        .register("mangrove_shelf", 3, ShelfBlockEntity::new, Blocks.MANGROVE_SHELF)
        .register("oak_shelf", 3, ShelfBlockEntity::new, Blocks.OAK_SHELF)
        .register("pale_oak_shelf", 3, ShelfBlockEntity::new, Blocks.PALE_OAK_SHELF)
        .register("spruce_shelf", 3, ShelfBlockEntity::new, Blocks.SPRUCE_SHELF)
        .register("warped_shelf", 3, ShelfBlockEntity::new, Blocks.WARPED_SHELF);

    new FixedPreviewProviderRegistry<>(registry, LecternPreviewProvider::new)
        .register("lectern", 1, (pos, state) -> new LecternBlockEntity(pos, state).bookAccess, Blocks.LECTERN);

    registry.register(ShulkerBoxTooltipUtil.id("ender_chest"), new EnderChestPreviewProvider(), Items.ENDER_CHEST);
    // @formatter:on
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
