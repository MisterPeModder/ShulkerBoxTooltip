package com.misterpemodder.shulkerboxtooltip.api;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.provider.OverridingPreviewProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

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
 * Registering a plugin (on NeoForge):
 * </p>
 * <pre>{@code
 * ModLoadingContext.get().registerExtensionPoint(ShulkerBoxTooltipPlugin.class,
 *     () -> new ShulkerBoxTooltipPlugin(MyModShulkerBoxTooltipPlugin::new));
 * }</pre>
 *
 * <p>
 * Registering a plugin (on Forge):
 * </p>
 * <pre>{@code
 * FMLJavaModLoadingContext context = // get instance from your mod's constructor
 * context.registerExtensionPoint(ShulkerBoxTooltipPlugin.class,
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
 * }}</pre>
 *
 * @since 1.3.0
 */
public interface ShulkerBoxTooltipApi {
  /**
   * Attempts to get the corresponding preview provider associated with the given item stack,
   * ignoring data-driven overrides.
   *
   * @param stack The stack
   * @return the associated {@link PreviewProvider} for the passed {@linkplain ItemStack}
   * @since 2.0.0
   */
  @Nullable
  static PreviewProvider getPreviewProviderForStack(ItemStack stack) {
    return PreviewProviderRegistry.getInstance().get(stack);
  }

  /**
   * Attempts to get the corresponding preview provider associated with the given item stack using
   * data-driven overrides if available.
   *
   * @param stack The stack
   * @return the associated {@link PreviewProvider} for the passed {@linkplain ItemStack}.
   * @since 4.2.0
   */
  @Nullable
  static PreviewProvider getPreviewProviderForStackWithOverrides(ItemStack stack) {
    return OverridingPreviewProvider.maybeWrap(getPreviewProviderForStack(stack), stack);
  }

  /**
   * (Client-only) Returns whether a preview is requested (see {@link #getCurrentPreviewType(boolean)})
   * and a preview is available for the given context.
   *
   * @param context The preview context.
   * @return true if the requested preview is available for display.
   * @since 2.0.0
   */
  @Environment(EnvType.CLIENT)
  static boolean isPreviewAvailable(PreviewContext context) {
    return getProviderIfPreviewAvailable(context) != null;
  }

  /**
   * (Client-only) Attempts to get the corresponding preview provider associated with the given item stack using
   * data-driven overrides if available AND a preview is requested (see {@link #getCurrentPreviewType(boolean)})
   * and a preview is available for the given context.
   * <p>
   * Same behavior as calling {@link #isPreviewAvailable(PreviewContext)} followed by {@link #getPreviewProviderForStackWithOverrides(ItemStack)}.
   *
   * @param context The preview context.
   * @return the associated {@link PreviewProvider} if the requested preview is available for display, null otherwise.
   * @since 5.4.0
   */
  @Nullable
  @Environment(EnvType.CLIENT)
  static PreviewProvider getProviderIfPreviewAvailable(PreviewContext context) {
    return ShulkerBoxTooltipClient.getProviderIfPreviewAvailable(context);
  }

  /**
   * (Client-only) Returns the currently requested preview type.
   * <p>
   * The requested preview type depends on factors like whether the preview keys are pressed,
   * or the preview is force-enabled through the config.
   *
   * @param hasFullPreviewMode Is the full preview mode available?
   * @return The preview type
   * @since 2.0.0
   */
  @Environment(EnvType.CLIENT)
  @Nonnull
  static PreviewType getCurrentPreviewType(boolean hasFullPreviewMode) {
    return ShulkerBoxTooltipClient.getCurrentPreviewType(hasFullPreviewMode);
  }

  /**
   * Checks whether the given player has ShulkerBoxTooltip installed and enabled server integration.
   *
   * @param player The player.
   * @return true if the player has the mod installed and server integration turned on.
   * @since 2.0.0
   */
  static boolean hasModAvailable(ServerPlayer player) {
    return ServerNetworking.hasModAvailable(player);
  }


  /**
   * (Client-only) Called on each entrypoint to register color keys and categories.
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
