package com.misterpemodder.shulkerboxtooltip.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.network.fabric.ServerNetworkingImpl;
import com.misterpemodder.shulkerboxtooltip.impl.provider.EnderChestPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.provider.PreviewProviderRegistryImpl;
import com.misterpemodder.shulkerboxtooltip.impl.provider.ShulkerBoxPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;
import java.util.stream.Collectors;

public class ShulkerBoxTooltipFabric implements ModInitializer, ShulkerBoxTooltipApi {
  private static boolean registeredProviders = false;

  /**
   * A list of all the vanilla shulker box items.
   */
  public static final Item[] SHULKER_BOX_ITEMS =
      new Item[] {Items.SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX,
          Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX,
          Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
          Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX,
          Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
          Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX};

  @Override
  public void onInitialize() {
    com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip.init();
    ServerNetworkingImpl.init();
  }

  private static void register(PreviewProviderRegistry registry, String id,
      PreviewProvider provider, Item... items) {
    registry.register(ShulkerBoxTooltipUtil.id(id), provider, items);
  }

  @Override
  public void registerProviders(PreviewProviderRegistry registry) {
    register(registry, "shulker_box", new ShulkerBoxPreviewProvider(), SHULKER_BOX_ITEMS);
    register(registry, "chest_like", new BlockEntityPreviewProvider(27, true), Items.CHEST,
        Items.TRAPPED_CHEST, Items.BARREL);
    register(registry, "furnace_like", new BlockEntityPreviewProvider(3, false, 1), Items.FURNACE,
        Items.BLAST_FURNACE, Items.SMOKER);
    register(registry, "dropper_like", new BlockEntityPreviewProvider(9, true, 3), Items.DROPPER,
        Items.DISPENSER);
    register(registry, "hopper", new BlockEntityPreviewProvider(5, true, 5), Items.HOPPER);
    register(registry, "brewing_stand", new BlockEntityPreviewProvider(5, false, 3),
        Items.BREWING_STAND);
    register(registry, "ender_chest", new EnderChestPreviewProvider(), Items.ENDER_CHEST);
  }

  /**
   * If not present, creates the preview item map by registering the preview providers supplied
   * by the API implementations.
   */
  public static void initPreviewItemsMap() {
    if (!registeredProviders) {
      registeredProviders = true;

      List<EntrypointContainer<ShulkerBoxTooltipApi>> plugins =
          FabricLoader.getInstance().getEntrypointContainers(ShulkerBoxTooltip.MOD_ID,
              ShulkerBoxTooltipApi.class);
      String pluginText = switch (plugins.size()) {
        case 0 -> "Loading %d plugins";
        case 1 -> "Loading %d plugin: %s";
        default -> "Loading %d plugins: %s";
      };

      ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME + "] " + pluginText,
          plugins.size(), plugins.stream().map(p -> p.getProvider().getMetadata().getId())
              .collect(Collectors.joining(", ")));
      for (EntrypointContainer<ShulkerBoxTooltipApi> container : plugins) {
        PreviewProviderRegistryImpl registry = PreviewProviderRegistryImpl.INSTANCE;
        int prevSize = registry.getIds().size();
        int registered;

        registry.setLocked(false);
        container.getEntrypoint().registerProviders(registry);
        registry.setLocked(true);
        registered = registry.getIds().size() - prevSize;

        String providerText = registered == 1 ?
            "Registered %d provider for mod %s" :
            "Registered %d providers for mod %s";

        ShulkerBoxTooltip.LOGGER.info("[" + ShulkerBoxTooltip.MOD_NAME + "] " + providerText, registered,
            container.getProvider().getMetadata().getId());
      }
    }
  }
}
