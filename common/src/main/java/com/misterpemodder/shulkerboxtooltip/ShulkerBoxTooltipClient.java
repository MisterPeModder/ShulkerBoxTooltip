package com.misterpemodder.shulkerboxtooltip;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.config.ClientConfiguration;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class ShulkerBoxTooltipClient {
  private static ItemStack previousStack = null;
  public static Minecraft client;
  private static boolean wasPreviewAccessed = false;

  private static boolean previewKeyPressed = false;
  private static boolean fullPreviewKeyPressed = false;
  private static boolean lockPreviewKeyPressed = false;

  private static boolean lockKeyHintsEnabled = false;

  public static void init() {
    client = Minecraft.getInstance();
    ClientNetworking.init();
  }

  public static ClientConfiguration getConfig() {
    return (ClientConfiguration) ShulkerBoxTooltip.config;
  }

  private static boolean isPreviewRequested() {
    return getConfig().preview.alwaysOn || ShulkerBoxTooltipClient.isPreviewKeyPressed();
  }

  private static List<Component> getTooltipHints(PreviewContext context, PreviewProvider provider) {
    if (!getConfig().preview.enable || !provider.shouldDisplay(context))
      return Collections.emptyList();

    boolean previewRequested = isPreviewRequested();
    List<Component> hints = new ArrayList<>();
    Component previewKeyHint = getPreviewKeyTooltipHint(context, provider, previewRequested);
    Component lockKeyHint = getLockKeyTooltipHint(context, provider, previewRequested);

    if (previewKeyHint != null)
      hints.add(previewKeyHint);
    if (lockKeyHint != null)
      hints.add(lockKeyHint);
    return hints;
  }

  @Nullable
  private static Component getPreviewKeyTooltipHint(PreviewContext context, PreviewProvider provider,
      boolean previewRequested) {
    if (previewRequested && ShulkerBoxTooltipClient.isFullPreviewKeyPressed())
      return null; // full preview is enabled, no need to display the preview key hint.

    // At this point, SHIFT may be pressed but not ALT.
    boolean fullPreviewAvailable = provider.isFullPreviewAvailable(context);

    if (!fullPreviewAvailable && previewRequested)
      return null;

    MutableComponent previewKeyHint = Component.literal("");
    Component previewKeyText = getConfig().controls.previewKey.get().getDisplayName();

    if (previewRequested) {
      previewKeyHint.append(getConfig().controls.fullPreviewKey.get().getDisplayName());
      if (!getConfig().preview.alwaysOn) {
        previewKeyHint.append("+").append(previewKeyText);
      }
    } else {
      previewKeyHint.append(previewKeyText);
    }
    previewKeyHint.append(": ");
    previewKeyHint.withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));

    String contentHint;

    if (ShulkerBoxTooltipApi.getCurrentPreviewType(fullPreviewAvailable) == PreviewType.NO_PREVIEW)
      contentHint = getConfig().preview.swapModes ?
          provider.getFullTooltipHintLangKey(context) :
          provider.getTooltipHintLangKey(context);
    else
      contentHint = getConfig().preview.swapModes ?
          provider.getTooltipHintLangKey(context) :
          provider.getFullTooltipHintLangKey(context);
    return previewKeyHint.append(
        Component.translatable(contentHint).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
  }

  @Nullable
  private static Component getLockKeyTooltipHint(PreviewContext context, PreviewProvider provider,
      boolean previewRequested) {
    if (!previewRequested || ShulkerBoxTooltipClient.isLockPreviewKeyPressed() || !lockKeyHintsEnabled)
      return null;
    MutableComponent lockKeyHint = Component.literal("");
    String lockKeyHintLangKey = provider.getLockKeyTooltipHintLangKey(context);

    lockKeyHint.append(ShulkerBoxTooltipClient.getConfig().controls.lockTooltipKey.get().getDisplayName());
    lockKeyHint.append(": ");
    lockKeyHint.withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
    lockKeyHint.append(
        Component.translatable(lockKeyHintLangKey).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
    return lockKeyHint;
  }

  public static void modifyStackTooltip(ItemStack stack, Consumer<Collection<Component>> tooltip) {
    if (client == null)
      return;

    PreviewContext context = PreviewContext.builder(stack).withOwner(client.player).build();
    TooltipDisplay tooltipDisplay = stack.getComponents().get(DataComponents.TOOLTIP_DISPLAY);
    PreviewProvider provider = ShulkerBoxTooltipApi.getPreviewProviderForStackWithOverrides(stack);

    if (provider == null || (tooltipDisplay != null && tooltipDisplay.hideTooltip()))
      return;
    if (previousStack == null || !ItemStack.matches(stack, previousStack))
      wasPreviewAccessed = false;
    previousStack = stack;

    if (!wasPreviewAccessed)
      provider.onInventoryAccessStart(context);
    wasPreviewAccessed = true;

    if (provider.showTooltipHints(context)) {
      if (getConfig().tooltip.type == Configuration.ShulkerBoxTooltipType.MOD)
        tooltip.accept(provider.addTooltip(context));
      if (getConfig().tooltip.showKeyHints) {
        tooltip.accept(getTooltipHints(context, provider));
      }
    }
  }

  public static boolean isPreviewAvailable(PreviewContext context) {
    if (getConfig().preview.enable) {
      ItemStack stack = context.stack();
      TooltipDisplay tooltipDisplay = stack.getComponents().get(DataComponents.TOOLTIP_DISPLAY);

      if (tooltipDisplay != null && tooltipDisplay.hideTooltip()) {
        return false;
      }
      PreviewProvider provider = ShulkerBoxTooltipApi.getPreviewProviderForStackWithOverrides(context.stack());

      return provider != null && provider.shouldDisplay(context) && ShulkerBoxTooltipApi.getCurrentPreviewType(
          provider.isFullPreviewAvailable(context)) != PreviewType.NO_PREVIEW;
    }
    return false;
  }

  public static PreviewType getCurrentPreviewType(boolean hasFullPreviewMode) {
    boolean previewRequested = isPreviewRequested();

    if (previewRequested && !hasFullPreviewMode) {
      return PreviewType.COMPACT;
    }
    if (getConfig().preview.swapModes) {
      if (previewRequested)
        return isFullPreviewKeyPressed() ? PreviewType.COMPACT : PreviewType.FULL;
    } else {
      if (previewRequested)
        return isFullPreviewKeyPressed() ? PreviewType.FULL : PreviewType.COMPACT;
    }
    return PreviewType.NO_PREVIEW;
  }

  public static boolean isPreviewKeyPressed() {
    return previewKeyPressed;
  }

  public static boolean isFullPreviewKeyPressed() {
    return fullPreviewKeyPressed;
  }

  public static boolean isLockPreviewKeyPressed() {
    return lockPreviewKeyPressed;
  }

  public static void setLockKeyHintsEnabled(boolean value) {
    lockKeyHintsEnabled = value;
  }

  private static boolean isKeyPressed(@Nullable Key key) {
    if (key == null || key.equals(Key.UNKNOWN_KEY) || key.isUnbound())
      return false;
    return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.get().getValue());
  }

  public static void updatePreviewKeys() {
    if (getConfig() == null) {
      previewKeyPressed = false;
      fullPreviewKeyPressed = false;
      lockPreviewKeyPressed = false;
    } else {
      previewKeyPressed = isKeyPressed(getConfig().controls.previewKey);
      fullPreviewKeyPressed = isKeyPressed(getConfig().controls.fullPreviewKey);
      lockPreviewKeyPressed = isKeyPressed(getConfig().controls.lockTooltipKey);
    }
  }
}
