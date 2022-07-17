package com.misterpemodder.shulkerboxtooltip.impl.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Contract;

public class ClientNetworking {
  @ExpectPlatform
  @SuppressWarnings("Contract")
  @Contract(value = " -> _")
  public static void init() {
    throw new AssertionError("Missing implementation of ClientNetworking.init()");
  }
}
