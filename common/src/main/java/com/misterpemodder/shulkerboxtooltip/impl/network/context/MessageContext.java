package com.misterpemodder.shulkerboxtooltip.impl.network.context;

import com.misterpemodder.shulkerboxtooltip.impl.network.channel.Channel;
import net.minecraft.entity.player.PlayerEntity;

public sealed interface MessageContext<MSG> permits C2SMessageContext, S2CMessageContext {
  /**
   * Executes the given task in the server/client's main thread.
   *
   * @param task The function to execute.
   */
  void execute(Runnable task);

  PlayerEntity getPlayer();

  Channel<MSG> getChannel();

  Side getReceivingSide();

  enum Side {
    CLIENT, SERVER
  }
}
