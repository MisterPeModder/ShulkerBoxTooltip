package com.misterpemodder.shulkerboxtooltip.forge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.forge.ShulkerBoxTooltipPlugin;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Contract;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mod(ShulkerBoxTooltip.MOD_ID)
@SuppressWarnings("unused")
public class ShulkerBoxTooltipImpl extends ShulkerBoxTooltip {
  public ShulkerBoxTooltipImpl() {
    super.onInitialize();
    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ShulkerBoxTooltipClientImpl::init);
    ModLoadingContext.get().registerExtensionPoint(ShulkerBoxTooltipPlugin.class, () -> new ShulkerBoxTooltipPlugin(() -> this));
  }

  /**
   * Implementation of {@link ShulkerBoxTooltip#isClient()}.
   */
  public static boolean isClient() {
    return FMLEnvironment.dist == Dist.CLIENT;
  }

  /**
   * Implementation of {@link ShulkerBoxTooltip#getConfigDir()}.
   */
  @Contract(value = " -> !null", pure = true)
  public static Path getConfigDir() {
    return FMLPaths.CONFIGDIR.get();
  }

  /**
   * Implementation of {@link ShulkerBoxTooltip#getPluginContainers()}.
   */
  @Contract(" -> !null")
  public static List<PluginContainer> getPluginContainers() {
    return ModList.get().applyForEachModContainer(
            modContainer -> modContainer.getCustomExtension(ShulkerBoxTooltipPlugin.class).map(
                extension -> new PluginContainer(modContainer.getModId(), extension.apiImplSupplier())))
        .flatMap(Optional::stream).collect(Collectors.toList());
  }
}
