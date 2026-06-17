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
import com.misterpemodder.shulkerboxtooltip.mixin.LecternBlockEntityAccessor;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.entity.*;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.List;

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
    List<String> colorPrefixes = ColorCollection.NAMES.map(n -> n + "_").asList();
    List<String> copperPrefixes = WeatheringCopperCollection.PREFIXES.asList();

    List<Block> dyedShulkerBoxes = Blocks.DYED_SHULKER_BOX.asList();
    List<Block> copperChests = Blocks.COPPER_CHEST.asList();

    // @formatter:off
    new FixedPreviewProviderRegistry<>(registry, ShulkerBoxPreviewProvider::new)
        .register("shulker_box", 9, ShulkerBoxBlockEntity::new, Blocks.SHULKER_BOX)
        .registerCollection("shulker_box", 9, ShulkerBoxBlockEntity::new, colorPrefixes, dyedShulkerBoxes);

    new FixedPreviewProviderRegistry<>(registry, InventoryAwarePreviewProvider::new)
        .register("chest", 9, ChestBlockEntity::new, Blocks.CHEST)
        .registerCollection("copper_chest", 9, ChestBlockEntity::new, copperPrefixes, copperChests)
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
        .register("lectern", 1, (pos, state) -> ((LecternBlockEntityAccessor)new LecternBlockEntity(pos, state)).getBookAccess(), Blocks.LECTERN);

    registry.register(ShulkerBoxTooltipUtil.id("ender_chest"), new EnderChestPreviewProvider(), Items.ENDER_CHEST);
    registry.register(ShulkerBoxTooltipUtil.id("bundle"), new BundlePreviewProvider(), Items.BUNDLE);
    ColorCollection.zipApply(ColorCollection.prefixWithColor(ColorCollection.create("bundle")), Items.DYED_BUNDLE,
        (String id, Item item) -> registry.register(ShulkerBoxTooltipUtil.id(id), new BundlePreviewProvider(), item));
    // @formatter:on
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void registerColors(ColorRegistry registry) {
    // @formatter:off
    registry.defaultCategory()
        .register(ColorKey.DEFAULT, "default")
        .register(ColorKey.ENDER_CHEST, "ender_chest", blockName("ender_chest"))
        .register(ColorKey.GENERIC_CONTAINER, "generic_container");
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
    registry.category(ShulkerBoxTooltipUtil.id("bundles"))
        .register(ColorKey.BUNDLE, "bundle", itemName("bundle"))
        .register(ColorKey.WHITE_BUNDLE, "white_bundle", itemName("white_bundle"))
        .register(ColorKey.ORANGE_BUNDLE, "orange_bundle", itemName("orange_bundle"))
        .register(ColorKey.MAGENTA_BUNDLE, "magenta_bundle", itemName("magenta_bundle"))
        .register(ColorKey.LIGHT_BLUE_BUNDLE, "light_blue_bundle", itemName("light_blue_bundle"))
        .register(ColorKey.YELLOW_BUNDLE, "yellow_bundle", itemName("yellow_bundle"))
        .register(ColorKey.LIME_BUNDLE, "lime_bundle", itemName("lime_bundle"))
        .register(ColorKey.PINK_BUNDLE, "pink_bundle", itemName("pink_bundle"))
        .register(ColorKey.GRAY_BUNDLE, "gray_bundle", itemName("gray_bundle"))
        .register(ColorKey.LIGHT_GRAY_BUNDLE, "light_gray_bundle", itemName("light_gray_bundle"))
        .register(ColorKey.CYAN_BUNDLE, "cyan_bundle", itemName("cyan_bundle"))
        .register(ColorKey.PURPLE_BUNDLE, "purple_bundle", itemName("purple_bundle"))
        .register(ColorKey.BLUE_BUNDLE, "blue_bundle", itemName("blue_bundle"))
        .register(ColorKey.BROWN_BUNDLE, "brown_bundle", itemName("brown_bundle"))
        .register(ColorKey.GREEN_BUNDLE, "green_bundle", itemName("green_bundle"))
        .register(ColorKey.RED_BUNDLE, "red_bundle", itemName("red_bundle"))
        .register(ColorKey.BLACK_BUNDLE, "black_bundle", itemName("black_bundle"));
    // @formatter:on
  }

  private static String blockName(String block) {
    return "block.minecraft." + block;
  }

  private static String itemName(String item) {
    return "item.minecraft." + item;
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
