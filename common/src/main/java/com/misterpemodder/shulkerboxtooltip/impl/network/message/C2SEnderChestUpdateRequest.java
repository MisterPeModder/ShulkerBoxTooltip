package com.misterpemodder.shulkerboxtooltip.impl.network.message;

import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Request an update to contents of the client's ender chest to the server.
 */
public record C2SEnderChestUpdateRequest() {
  public static class Type implements MessageType<C2SEnderChestUpdateRequest> {
    @Override
    public void encode(C2SEnderChestUpdateRequest message, PacketByteBuf buf) {
    }

    @Override
    public C2SEnderChestUpdateRequest decode(PacketByteBuf buf) {
      return new C2SEnderChestUpdateRequest();
    }

    @Override
    public void onReceive(C2SEnderChestUpdateRequest message, MessageContext<C2SEnderChestUpdateRequest> context) {
      var player = (ServerPlayerEntity) context.getPlayer();
      S2CMessages.ENDER_CHEST_UPDATE.sendTo(player, S2CEnderChestUpdate.create(player.getEnderChestInventory()));
    }
  }
}
