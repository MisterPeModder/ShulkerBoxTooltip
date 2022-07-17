package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class ClientNetworking {
  @Nullable
  protected static ProtocolVersion serverProtocolVersion;

  protected static void onJoinServer(MinecraftClient client) {
    client.execute(ShulkerBoxTooltip::initPreviewItemsMap);
    ShulkerBoxTooltip.config = ConfigurationHandler.copyOf(ShulkerBoxTooltip.savedConfig);

    // Re-init some config values before syncing
    serverProtocolVersion = null;
    if (!MinecraftClient.getInstance().isIntegratedServerRunning())
      ConfigurationHandler.reinitClientSideSyncedValues(ShulkerBoxTooltip.config);
  }

  @ExpectPlatform
  @SuppressWarnings("Contract")
  @Contract(value = " -> _")
  public static void init() {
    throw new AssertionError("Missing implementation of ClientNetworking.init()");
  }
}
