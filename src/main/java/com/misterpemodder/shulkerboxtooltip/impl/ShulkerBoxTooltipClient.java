package com.misterpemodder.shulkerboxtooltip.impl;

import java.util.List;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.ShulkerBoxTooltipType;
import com.misterpemodder.shulkerboxtooltip.impl.hook.ShulkerPreviewPosGetter;
import com.misterpemodder.shulkerboxtooltip.impl.network.server.S2CPacketTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public final class ShulkerBoxTooltipClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    S2CPacketTypes.register();
  }

  public static boolean shouldDisplayPreview() {
    return ShulkerBoxTooltip.config.main.alwaysOn || Screen.hasShiftDown();
  }

  @Nullable
  public static Text getTooltipHint(ItemStack stack, PreviewProvider provider) {
    boolean shouldDisplay = shouldDisplayPreview();

    if (!ShulkerBoxTooltip.config.main.enablePreview || !provider.shouldDisplay(stack)
        || (shouldDisplay && Screen.hasAltDown()))
      return null;

    // At this point, SHIFT may be pressed but not ALT.
    boolean fullPreviewAvailable = provider.isFullPreviewAvailable(stack);

    if (!fullPreviewAvailable && shouldDisplay)
      return null;

    String keyHint =
        shouldDisplay ? (ShulkerBoxTooltip.config.main.alwaysOn ? "Alt" : "Alt+Shift") : "Shift";
    String contentHint;

    if (ShulkerBoxTooltipApi.getCurrentPreviewType(fullPreviewAvailable) == PreviewType.NO_PREVIEW)
      contentHint =
          ShulkerBoxTooltip.config.main.swapModes ? provider.getFullTooltipHintLangKey(stack)
              : provider.getTooltipHintLangKey(stack);
    else
      contentHint = ShulkerBoxTooltip.config.main.swapModes ? provider.getTooltipHintLangKey(stack)
          : provider.getFullTooltipHintLangKey(stack);
    return new LiteralText(keyHint + ": ").setStyle(new Style().setColor(Formatting.GOLD))
        .append(new TranslatableText(contentHint).setStyle(new Style().setColor(Formatting.WHITE)));
  }

  public static void drawShulkerBoxPreview(Screen screen, ItemStack stack) {
    PreviewProvider provider = ShulkerBoxTooltipApi.getPreviewProviderForStack(stack);

    if (provider == null)
      return;

    PreviewRenderer renderer = provider.getRenderer();

    if (renderer == null)
      renderer = PreviewRenderer.getDefaultRendererInstance();
    renderer.setPreview(stack, provider);
    renderer.setPreviewType(
        ShulkerBoxTooltipApi.getCurrentPreviewType(provider.isFullPreviewAvailable(stack)));

    int x = Math.min(((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getStartX() - 1,
        screen.width - renderer.getWidth());
    int y = ((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getBottomY() + 1;
    int h = renderer.getHeight();

    if (ShulkerBoxTooltip.config.main.lockPreview || y + h > screen.height)
      y = ((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getTopY() - h;
    renderer.draw(x, y);
  }

  public static void modifyStackTooltip(ItemStack stack, List<Text> tooltip) {
    PreviewProvider provider = ShulkerBoxTooltipApi.getPreviewProviderForStack(stack);

    if (provider != null && provider.showTooltipHints(stack)) {
      if (ShulkerBoxTooltip.config.main.tooltipType == ShulkerBoxTooltipType.MOD)
        tooltip.addAll(provider.addTooltip(stack));
      if (ShulkerBoxTooltip.config.main.showKeyHints) {
        Text hint = ShulkerBoxTooltipClient.getTooltipHint(stack, provider);

        if (hint != null)
          tooltip.add(hint);
      }
    }
  }
}
