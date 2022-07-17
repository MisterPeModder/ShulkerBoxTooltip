package com.misterpemodder.shulkerboxtooltip.impl.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Contract;

public class ServerNetworking {
  @ExpectPlatform
  @SuppressWarnings("Contract")
  @Contract(" -> _")
  public static void init() {
    throw new AssertionError("Missing implementation of ServerNetworking.init()");
  }

  @ExpectPlatform
  @SuppressWarnings({"Contract", "unused"})
  @Contract("_ -> _")
  public static boolean hasModAvailable(ServerPlayerEntity player) {
    throw new AssertionError("Missing implementation of ServerNetworking.hasModAvailable()");
  }
}
