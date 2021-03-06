package com.misterpemodder.shulkerboxtooltip.impl;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.ShulkerBoxTooltipType;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import io.github.cottonmc.cotton.gui.client.LibGui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public final class ShulkerBoxTooltipClient implements ClientModInitializer {
  private static ItemStack previousStack = null;
  public static MinecraftClient client;
  private static boolean wasPreviewAccessed = false;
  private static Supplier<Boolean> darkModeSupplier;

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
    return ShulkerBoxTooltip.config.preview.alwaysOn || ShulkerBoxTooltipApi.isPreviewKeyPressed();
  }

  @Nullable
  public static Text getTooltipHint(PreviewContext context, PreviewProvider provider) {
    boolean shouldDisplay = shouldDisplayPreview();

    if (!ShulkerBoxTooltip.config.preview.enable || !provider.shouldDisplay(context)
        || (shouldDisplay && ShulkerBoxTooltipApi.isFullPreviewKeyPressed()))
      return null;

    // At this point, SHIFT may be pressed but not ALT.
    boolean fullPreviewAvailable = provider.isFullPreviewAvailable(context);

    if (!fullPreviewAvailable && shouldDisplay)
      return null;

    MutableText keyHint = new LiteralText("");
    Text previewKeyText = ShulkerBoxTooltip.config.controls.previewKey.get().getLocalizedText();

    if (shouldDisplay) {
      keyHint.append(ShulkerBoxTooltip.config.controls.fullPreviewKey.get().getLocalizedText());
      if (!ShulkerBoxTooltip.config.preview.alwaysOn) {
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
          ShulkerBoxTooltip.config.preview.swapModes ? provider.getFullTooltipHintLangKey(context)
              : provider.getTooltipHintLangKey(context);
    else
      contentHint =
          ShulkerBoxTooltip.config.preview.swapModes ? provider.getTooltipHintLangKey(context)
              : provider.getFullTooltipHintLangKey(context);
    return keyHint.append(
        new TranslatableText(contentHint).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
  }

  public static void modifyStackTooltip(ItemStack stack, List<Text> tooltip) {
    if (ShulkerBoxTooltipClient.client == null)
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
      if (ShulkerBoxTooltip.config.tooltip.type == ShulkerBoxTooltipType.MOD)
        tooltip.addAll(provider.addTooltip(context));
      if (ShulkerBoxTooltip.config.tooltip.showKeyHints) {
        Text hint = ShulkerBoxTooltipClient.getTooltipHint(context, provider);

        if (hint != null)
          tooltip.add(hint);
      }
    }
  }

  public static boolean isDarkModeEnabled() {
    return darkModeSupplier.get();
  }
}
