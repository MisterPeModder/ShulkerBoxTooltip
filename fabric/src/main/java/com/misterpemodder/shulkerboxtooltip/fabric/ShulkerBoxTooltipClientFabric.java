package com.misterpemodder.shulkerboxtooltip.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.ShulkerBoxTooltipType;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import io.github.cottonmc.cotton.gui.client.LibGui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public final class ShulkerBoxTooltipClientFabric implements ClientModInitializer {
  private static ItemStack previousStack = null;
  public static MinecraftClient client;
  private static boolean wasPreviewAccessed = false;
  private static Supplier<Boolean> darkModeSupplier;

  private static boolean previewKeyPressed = false;
  private static boolean fullPreviewKeyPressed = false;

  @Override
  public void onInitializeClient() {
    client = MinecraftClient.getInstance();
    ClientNetworking.init();
    if (FabricLoader.getInstance().isModLoaded("libgui")) {
      ShulkerBoxTooltip.LOGGER
          .info("[" + ShulkerBoxTooltip.MOD_NAME + "] Found LibGui, enabling integration");
      darkModeSupplier = LibGui::isDarkMode;
    } else {
      darkModeSupplier = () -> false;
    }
  }

  public static boolean shouldDisplayPreview() {
    return ShulkerBoxTooltipFabric.config.preview.alwaysOn || ShulkerBoxTooltipApi.isPreviewKeyPressed();
  }

  @Nullable
  public static Text getTooltipHint(PreviewContext context, PreviewProvider provider) {
    boolean shouldDisplay = shouldDisplayPreview();

    if (!ShulkerBoxTooltipFabric.config.preview.enable || !provider.shouldDisplay(context)
        || (shouldDisplay && ShulkerBoxTooltipApi.isFullPreviewKeyPressed()))
      return null;

    // At this point, SHIFT may be pressed but not ALT.
    boolean fullPreviewAvailable = provider.isFullPreviewAvailable(context);

    if (!fullPreviewAvailable && shouldDisplay)
      return null;

    MutableText keyHint = new LiteralText("");
    Text previewKeyText = ShulkerBoxTooltipFabric.config.controls.previewKey.get().getLocalizedText();

    if (shouldDisplay) {
      keyHint.append(ShulkerBoxTooltipFabric.config.controls.fullPreviewKey.get().getLocalizedText());
      if (!ShulkerBoxTooltipFabric.config.preview.alwaysOn) {
        keyHint.append("+").append(previewKeyText);
      }
    } else {
      keyHint.append(previewKeyText);
    }
    keyHint.append(": ");
    keyHint.fillStyle(Style.EMPTY.withColor(Formatting.GOLD));

    String contentHint;

    if (ShulkerBoxTooltipApi.getCurrentPreviewType(fullPreviewAvailable) == PreviewType.NO_PREVIEW)
      contentHint =
          ShulkerBoxTooltipFabric.config.preview.swapModes ? provider.getFullTooltipHintLangKey(context)
              : provider.getTooltipHintLangKey(context);
    else
      contentHint =
          ShulkerBoxTooltipFabric.config.preview.swapModes ? provider.getTooltipHintLangKey(context)
              : provider.getFullTooltipHintLangKey(context);
    return keyHint.append(
        new TranslatableText(contentHint).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
  }

  public static void modifyStackTooltip(ItemStack stack, List<Text> tooltip) {
    if (ShulkerBoxTooltipClientFabric.client == null)
      return;

    PreviewContext context = PreviewContext.of(stack, client.player);
    PreviewProvider provider = ShulkerBoxTooltipApi.getPreviewProviderForStack(stack);

    if (provider == null)
      return;
    if (previousStack == null || !ItemStack.areEqual(stack, previousStack))
      wasPreviewAccessed = false;
    previousStack = stack;

    if (!wasPreviewAccessed)
      provider.onInventoryAccessStart(context);
    wasPreviewAccessed = true;

    if (provider.showTooltipHints(context)) {
      if (ShulkerBoxTooltipFabric.config.tooltip.type == ShulkerBoxTooltipType.MOD)
        tooltip.addAll(provider.addTooltip(context));
      if (ShulkerBoxTooltipFabric.config.tooltip.showKeyHints) {
        Text hint = ShulkerBoxTooltipClientFabric.getTooltipHint(context, provider);

        if (hint != null)
          tooltip.add(hint);
      }
    }
  }

  public static boolean isDarkModeEnabled() {
    return darkModeSupplier.get();
  }

  public static boolean isPreviewKeyPressed() {
    return previewKeyPressed;
  }

  public static boolean isFullPreviewKeyPressed() {
    return fullPreviewKeyPressed;
  }

  private static boolean isKeyPressed(@Nullable Key key) {
    if (key == null || key.equals(Key.UNKNOWN_KEY) || key.get().equals(InputUtil.UNKNOWN_KEY))
      return false;
    return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key.get().getCode());
  }

  public static void updatePreviewKeys() {
    Configuration config = ShulkerBoxTooltipFabric.config;

    if (config == null) {
      previewKeyPressed = false;
      fullPreviewKeyPressed = false;
    } else {
      previewKeyPressed = isKeyPressed(config.controls.previewKey);
      fullPreviewKeyPressed = isKeyPressed(config.controls.fullPreviewKey);
    }
  }
}
