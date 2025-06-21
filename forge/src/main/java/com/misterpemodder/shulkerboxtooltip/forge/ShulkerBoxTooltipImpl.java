package com.misterpemodder.shulkerboxtooltip.forge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.forge.ShulkerBoxTooltipPlugin;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Contract;

import java.nio.file.Path;

@Mod(ShulkerBoxTooltip.MOD_ID)
@Mod.EventBusSubscriber(modid = ShulkerBoxTooltip.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("unused")
public class ShulkerBoxTooltipImpl extends ShulkerBoxTooltip {
  public static ShulkerBoxTooltipImpl INSTANCE = null;
  public final FMLJavaModLoadingContext context;

  public ShulkerBoxTooltipImpl(FMLJavaModLoadingContext context) {
    INSTANCE = this;
    this.context = context;
  }

  @SubscribeEvent
  public static void onSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      INSTANCE.context.registerExtensionPoint(ShulkerBoxTooltipPlugin.class,
          () -> new ShulkerBoxTooltipPlugin(() -> INSTANCE));
      ShulkerBoxTooltip.init();
    });
  }

  /**
   * Implementation of {@link ShulkerBoxTooltip#getConfigDir()}.
   */
  @Contract(value = " -> !null", pure = true)
  public static Path getConfigDir() {
    return FMLPaths.CONFIGDIR.get();
  }
}
