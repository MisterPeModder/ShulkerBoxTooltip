package com.misterpemodder.shulkerboxtooltip.api;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Implement this interface and use this as your entrypoint.
 *
 * <p>
 * Example plugin: register a preview for barrels
 * </p>
 * <pre>{@code
 *   public class MyModShulkerBoxTooltipPlugin implements ShulkerBoxTooltipApi {
 *     @Override
 *     public void registerProviders(PreviewProviderRegistry registry) {
 *       registry.register(new Identifier("mymod", "barrel_example"),
 *           new BlockEntityPreviewProvider(27, true), Items.BARREL);
 *     }
 *   }
 * }</pre>
 *
 * <p>
 * Registering a plugin (on Forge):
 * </p>
 * <pre>{@code
 * ModLoadingContext.get().registerExtensionPoint(ShulkerBoxTooltipPlugin.class,
 *     () -> new ShulkerBoxTooltipPlugin(MyModShulkerBoxTooltipPlugin::new));
 * }</pre>
 *
 * <p>
 * Registering a plugin (on Fabric):
 * Inside the fabric.mod.json file, add:
 * </p>
 * <pre>{@code
 * {
 *   "entrypoints": {
 *     "shulkerboxtooltip": [
 *       "com.mymod.MyModShulkerBoxTooltipPlugin"
 *     ]
 *   }
 * }
 * }</pre>
 *
 * @since 1.3.0
 */
public interface ShulkerBoxTooltipApi {
  /**
   * Attempts to get the corresponding preview provider associated with the given item stack.
   *
   * @param stack The stack
   * @return the associated {@link PreviewProvider} for the passed {@linkplain ItemStack}.
   * @since 2.0.0
   */
  @Nullable
  static PreviewProvider getPreviewProviderForStack(ItemStack stack) {
    return PreviewProviderRegistry.getInstance().get(stack);
  }

  /**
   * Is there a preview available for the given preview context?
   *
   * @param context The preview context.
   * @return true if there is a preview available
   * @since 2.0.0
   */
  @Environment(EnvType.CLIENT)
  static boolean isPreviewAvailable(PreviewContext context) {
    if (ShulkerBoxTooltip.config.preview.enable) {
      PreviewProvider provider = getPreviewProviderForStack(context.stack());

      return provider != null && provider.shouldDisplay(context) && ShulkerBoxTooltipApi.getCurrentPreviewType(
          provider.isFullPreviewAvailable(context)) != PreviewType.NO_PREVIEW;
    }
    return false;
  }

  /**
   * Gets the type of item preview to use.
   *
   * @param hasFullPreviewMode Is the full preview mode available?
   * @return The preview type depending on which keys are pressed.
   * @since 2.0.0
   */
  @Environment(EnvType.CLIENT)
  @Nonnull
  static PreviewType getCurrentPreviewType(boolean hasFullPreviewMode) {
    boolean shouldDisplay = ShulkerBoxTooltipClient.shouldDisplayPreview();

    if (shouldDisplay && !hasFullPreviewMode) {
      return PreviewType.COMPACT;
    }
    if (ShulkerBoxTooltip.config.preview.swapModes) {
      if (shouldDisplay)
        return isFullPreviewKeyPressed() ? PreviewType.COMPACT : PreviewType.FULL;
    } else {
      if (shouldDisplay)
        return isFullPreviewKeyPressed() ? PreviewType.FULL : PreviewType.COMPACT;
    }
    return PreviewType.NO_PREVIEW;
  }

  /**
   * Checks whether the client player is pressing the preview key ({@code shift} by default).
   *
   * @return true if the preview key is pressed.
   * @since 2.1.0
   */
  @Environment(EnvType.CLIENT)
  static boolean isPreviewKeyPressed() {
    return ShulkerBoxTooltipClient.isPreviewKeyPressed();
  }

  /**
   * Checks whether the client player is pressing the full preview key ({@code alt} by default).
   *
   * @return true if the full preview key is pressed.
   * @since 2.1.0
   */
  @Environment(EnvType.CLIENT)
  static boolean isFullPreviewKeyPressed() {
    return ShulkerBoxTooltipClient.isFullPreviewKeyPressed();
  }

  /**
   * Checks whether the given player has ShulkerBoxTooltip installed and enabled server integration.
   *
   * @param player The player.
   * @return true if the player has the mod installed and server integration turned on.
   * @since 2.0.0
   */
  static boolean hasModAvailable(ServerPlayerEntity player) {
    return ServerNetworking.hasModAvailable(player);
  }


  /**
   * Called on each entrypoint to register color keys and categories.
   * <p>
   * While registering color keys is optional, it allows them to be customized be the users though the configuration screen/file.
   * <p>
   * ShulkerBoxTooltip is guaranteed to always call this method on all plugins before calling {@link #registerProviders(PreviewProviderRegistry)}.
   *
   * @param registry The color registry instance.
   * @since 3.2.0
   */
  @Environment(EnvType.CLIENT)
  default void registerColors(ColorRegistry registry) {
  }

  /**
   * Called on each entrypoint to register preview providers.
   *
   * @param registry The provider registry instance..
   * @since 3.0.0
   */
  void registerProviders(PreviewProviderRegistry registry);
}
