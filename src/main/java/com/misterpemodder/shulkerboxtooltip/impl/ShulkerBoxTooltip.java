package com.misterpemodder.shulkerboxtooltip.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.ShulkerBoxTooltipType;
import com.misterpemodder.shulkerboxtooltip.impl.config.LegacyConfiguration;
import com.misterpemodder.shulkerboxtooltip.impl.hook.ShulkerPreviewPosGetter;
import com.misterpemodder.shulkerboxtooltip.impl.provider.FurnacePreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.provider.ShulkerBoxPreviewProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormat;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public final class ShulkerBoxTooltip implements ClientModInitializer, ShulkerBoxTooltipApi {
  public static Configuration config;

  public static String MOD_ID = "shulkerboxtooltip";
  public static final Logger LOGGER = LogManager.getLogger("ShulkerBoxTooltip");
  private static Map<Item, PreviewProvider> previewItems = null;

  @Override
  public void onInitializeClient() {
    AutoConfig.register(Configuration.class, GsonConfigSerializer::new);
    config = AutoConfig.getConfigHolder(Configuration.class).getConfig();
    LegacyConfiguration.updateConfig();
  }

  @Override
  public String getModId() {
    return MOD_ID;
  }

  @Override
  public void registerProviders(Map<PreviewProvider, List<Item>> providers) {
    providers.put(new ShulkerBoxPreviewProvider(),
        SystemUtil.consume(new ArrayList<Item>(), items -> {
          items.add(Items.SHULKER_BOX);
          items.add(Items.WHITE_SHULKER_BOX);
          items.add(Items.ORANGE_SHULKER_BOX);
          items.add(Items.MAGENTA_SHULKER_BOX);
          items.add(Items.LIGHT_BLUE_SHULKER_BOX);
          items.add(Items.YELLOW_SHULKER_BOX);
          items.add(Items.LIME_SHULKER_BOX);
          items.add(Items.PINK_SHULKER_BOX);
          items.add(Items.GRAY_SHULKER_BOX);
          items.add(Items.LIGHT_GRAY_SHULKER_BOX);
          items.add(Items.CYAN_SHULKER_BOX);
          items.add(Items.PURPLE_SHULKER_BOX);
          items.add(Items.BLUE_SHULKER_BOX);
          items.add(Items.BROWN_SHULKER_BOX);
          items.add(Items.GREEN_SHULKER_BOX);
          items.add(Items.RED_SHULKER_BOX);
          items.add(Items.BLACK_SHULKER_BOX);
        }));
    providers.put(new BlockEntityPreviewProvider(27, true),
        Arrays.asList(Items.CHEST, Items.TRAPPED_CHEST, Items.BARREL));
    providers.put(new FurnacePreviewProvider(),
        Arrays.asList(Items.FURNACE, Items.BLAST_FURNACE, Items.SMOKER));
    providers.put(new BlockEntityPreviewProvider(9, true),
        Arrays.asList(Items.DISPENSER, Items.DROPPER));
    providers.put(new BlockEntityPreviewProvider(5, true), Collections.singletonList(Items.HOPPER));
    providers.put(new BlockEntityPreviewProvider(5, false),
        Collections.singletonList(Items.BREWING_STAND));
  }


  /**
   * Modifies the shulker box tooltip.
   * 
   * @param stack    The shulker box item stack
   * @param tooltip  The list to put the tooltip in.
   * @param compound The stack NBT data.
   * @return true to cancel vanilla tooltip code, false otherwise.
   */
  public static boolean buildShulkerBoxTooltip(ItemStack stack, List<Component> tooltip,
      @Nullable CompoundTag compound) {
    ShulkerBoxTooltipType type = config.main.tooltipType;
    if (type == ShulkerBoxTooltipType.NONE)
      return true;
    if (type == ShulkerBoxTooltipType.VANILLA)
      return false;
    if (compound == null) {
      tooltip.add(
          new TranslatableComponent("container.shulkerbox.empty").applyFormat(ChatFormat.GRAY));
    } else if (compound.containsKey("LootTable", 8)) {
      tooltip.add(new TextComponent(ChatFormat.GRAY + "???????"));
    } else if (compound.containsKey("Items", 9)) {
      ListTag list = compound.getList("Items", 10);
      if (list.size() > 0) {
        tooltip.add(new TranslatableComponent("container.shulkerbox.contains", list.size())
            .applyFormat(ChatFormat.GRAY));
      } else {
        tooltip.add(
            new TranslatableComponent("container.shulkerbox.empty").applyFormat(ChatFormat.GRAY));
      }
    }
    return true;
  }

  private static boolean shouldDisplayPreview() {
    return config.main.alwaysOn || Screen.hasShiftDown();
  }

  @Nullable
  public static Component getTooltipHint(ItemStack stack, PreviewProvider provider) {
    boolean shouldDisplay = shouldDisplayPreview();
    if (!config.main.enablePreview || !provider.shouldDisplay(stack)
        || (shouldDisplay && Screen.hasAltDown()))
      return null;
    // At this point, SHIFT may be pressed but not ALT.
    boolean fullPreviewAvailable = provider.isFullPreviewAvailable(stack);
    if (!fullPreviewAvailable && shouldDisplay)
      return null;
    String keyHint = shouldDisplay ? (config.main.alwaysOn ? "Alt" : "Alt+Shift") : "Shift";
    String contentHint;
    if (getCurrentPreviewType(fullPreviewAvailable) == PreviewType.NO_PREVIEW)
      contentHint = config.main.swapModes ? provider.getFullTooltipHintLangKey(stack)
          : provider.getTooltipHintLangKey(stack);
    else
      contentHint = config.main.swapModes ? provider.getTooltipHintLangKey(stack)
          : provider.getFullTooltipHintLangKey(stack);
    return new TextComponent(keyHint + ": ").applyFormat(ChatFormat.GOLD)
        .append(new TranslatableComponent(contentHint).applyFormat(ChatFormat.WHITE));
  }

  /**
   * @param hasFullPreviewMode Is the full preview mode available?
   * @return The shulker box tooltip type depending of which keys are pressed.
   */
  public static PreviewType getCurrentPreviewType(boolean hasFullPreviewMode) {
    boolean shouldDisplay = shouldDisplayPreview();
    if (shouldDisplay && !hasFullPreviewMode) {
      return PreviewType.COMPACT;
    }
    if (config.main.swapModes) {
      if (shouldDisplay)
        return Screen.hasAltDown() ? PreviewType.COMPACT : PreviewType.FULL;
    } else {
      if (shouldDisplay)
        return Screen.hasAltDown() ? PreviewType.FULL : PreviewType.COMPACT;
    }
    return PreviewType.NO_PREVIEW;
  }

  /**
   * Should the preview be drawn?
   * 
   * @param stack The stack to check.
   * @return true if the preview should be drawn.
   */
  public static boolean hasShulkerBoxPreview(ItemStack stack) {
    if (config.main.enablePreview) {
      PreviewProvider provider = getPreviewProviderForStack(stack);
      return provider != null && provider.shouldDisplay(stack) && getCurrentPreviewType(
          provider.isFullPreviewAvailable(stack)) != PreviewType.NO_PREVIEW;
    }
    return false;
  }

  public static void initPreviewItemsMap() {
    if (previewItems == null) {
      previewItems = buildPreviewItemsMap();
    }
  }

  private static Map<Item, PreviewProvider> buildPreviewItemsMap() {
    Map<Item, PreviewProvider> previewItems = new HashMap<>();
    List<ShulkerBoxTooltipApi> apiImpls = FabricLoader.getInstance()
        .getEntrypoints(ShulkerBoxTooltip.MOD_ID, ShulkerBoxTooltipApi.class);
    Map<PreviewProvider, List<Item>> providers = new HashMap<>();

    for (ShulkerBoxTooltipApi impl : apiImpls) {
      impl.registerProviders(providers);
      for (Entry<PreviewProvider, List<Item>> entry : providers.entrySet()) {
        for (Item item : entry.getValue()) {
          previewItems.put(item, entry.getKey());
        }
      }
      providers.clear();
    }
    return previewItems;
  }

  /**
   * @return the associated {@link PreviewProvider} for the passed {@link ItemStack}.
   */
  @Nullable
  public static PreviewProvider getPreviewProviderForStack(ItemStack stack) {
    return previewItems == null ? null : previewItems.get(stack.getItem());
  }

  public static void drawShulkerBoxPreview(Screen screen, ItemStack stack, int mouseX, int mouseY) {
    PreviewProvider provider = getPreviewProviderForStack(stack);
    if (provider == null)
      return;
    PreviewRenderer renderer = provider.getRenderer();
    if (renderer == null)
      renderer = PreviewRenderer.getDefaultRendererInstance();
    renderer.setPreview(stack, provider);
    renderer.setPreviewType(getCurrentPreviewType(provider.isFullPreviewAvailable(stack)));
    int x = Math.min(((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getStartX() - 1,
        screen.width - renderer.getWidth());
    int y = ((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getBottomY() + 1;
    int h = renderer.getHeight();
    if (config.main.lockPreview || y + h > screen.height)
      y = ((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getTopY() - h;
    renderer.draw(x, y);
  }
}
