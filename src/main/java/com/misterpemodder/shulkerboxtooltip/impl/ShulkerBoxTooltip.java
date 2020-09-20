package com.misterpemodder.shulkerboxtooltip.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.network.client.C2SPacketTypes;
import com.misterpemodder.shulkerboxtooltip.impl.provider.EnderChestPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.provider.ShulkerBoxPreviewProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public final class ShulkerBoxTooltip implements ModInitializer, ShulkerBoxTooltipApi {
  public static String MOD_ID = "shulkerboxtooltip";
  public static final Logger LOGGER = LogManager.getLogger("ShulkerBoxTooltip");

  /**
   * The active config object, some of its properties are synced with the server.
   */
  public static Configuration config;
  /**
   * the actual config object, its values are never synced.
   */
  public static Configuration savedConfig;
  public static boolean synchronisedWithServer = true;

  private static Map<Item, PreviewProvider> previewItems = null;

  /**
   * A list of all the vanilla shulker box items.
   */
  public static final ImmutableList<Item> SHULKER_BOX_ITEMS = ImmutableList.of(Items.SHULKER_BOX,
      Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX,
      Items.YELLOW_SHULKER_BOX, Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
      Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.BLUE_SHULKER_BOX,
      Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX);

  @Override
  public void onInitialize() {
    savedConfig = Configuration.register();
    config = Configuration.copyFrom(savedConfig);
    if (config.server.clientIntegration)
      C2SPacketTypes.register();
  }

  @Override
  public String getModId() {
    return ShulkerBoxTooltip.MOD_ID;
  }

  @Override
  public void registerProviders(Map<PreviewProvider, List<Item>> providers) {
    providers.put(new ShulkerBoxPreviewProvider(), new ArrayList<>(SHULKER_BOX_ITEMS));
    providers.put(new BlockEntityPreviewProvider(27, true),
        Arrays.asList(Items.CHEST, Items.TRAPPED_CHEST, Items.BARREL));
    providers.put(new BlockEntityPreviewProvider(3, false, 1),
        Arrays.asList(Items.FURNACE, Items.BLAST_FURNACE, Items.SMOKER));
    providers.put(new BlockEntityPreviewProvider(9, true, 3), Arrays.asList(Items.DISPENSER, Items.DROPPER));
    providers.put(new BlockEntityPreviewProvider(5, true, 5), Collections.singletonList(Items.HOPPER));
    providers.put(new BlockEntityPreviewProvider(5, false, 3), Collections.singletonList(Items.BREWING_STAND));
    providers.put(new EnderChestPreviewProvider(), Collections.singletonList(Items.ENDER_CHEST));
  }

  @Nullable
  public static Map<Item, PreviewProvider> getPreviewItems() {
    return previewItems;
  }

  /**
   * If not present, creates the preview item map by registering the preview providers supplied
   * by the API implementations.
   */
  public static void initPreviewItemsMap() {
    if (previewItems == null) {
      List<ShulkerBoxTooltipApi> apiImpls = FabricLoader.getInstance().getEntrypoints(ShulkerBoxTooltip.MOD_ID,
          ShulkerBoxTooltipApi.class);
      Map<PreviewProvider, List<Item>> providers = new HashMap<>();

      previewItems = new HashMap<>();
      for (ShulkerBoxTooltipApi impl : apiImpls) {
        impl.registerProviders(providers);
        for (Map.Entry<PreviewProvider, List<Item>> entry : providers.entrySet()) {
          for (Item item : entry.getValue()) {
            previewItems.put(item, entry.getKey());
          }
        }
        providers.clear();
      }
    }
  }
}
