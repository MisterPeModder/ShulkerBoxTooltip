package com.misterpemodder.shulkerboxtooltip.impl.network.message;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ProtocolVersion;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.C2SChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * Initiates a handshake with the server.
 * <p>
 * Sent by clients as soon as the server registers the capability to handle it.
 *
 * @param clientVersion The protocol version of the client.
 */
public record C2SHandshakeStart(ProtocolVersion clientVersion) {
  public static class Type implements MessageType<C2SHandshakeStart> {

    @Override
    public void encode(C2SHandshakeStart message, FriendlyByteBuf buf) {
      message.clientVersion.writeToPacketBuf(buf);
    }

    @Override
    public C2SHandshakeStart decode(FriendlyByteBuf buf) {
      return new C2SHandshakeStart(ProtocolVersion.readFromPacketBuf(buf));
    }

    @Override
    public void onReceive(C2SHandshakeStart message, MessageContext<C2SHandshakeStart> context) {
      var player = (ServerPlayer) context.getPlayer();
      var channel = (C2SChannel<C2SHandshakeStart>) context.getChannel();

      if (message.clientVersion == null) {
        ShulkerBoxTooltip.LOGGER.error(player.getGameProfile().name() + ": received invalid handshake packet");
        channel.unregisterFor(player);
        return;
      }

      // compatibility check
      ShulkerBoxTooltip.LOGGER.info(player.getGameProfile().name() + ": protocol version: " + message.clientVersion);
      if (message.clientVersion.major() != ProtocolVersion.CURRENT.major()) {
        ShulkerBoxTooltip.LOGGER.error(
            player.getGameProfile().name() + ": incompatible client protocol version, expected "
            + ProtocolVersion.CURRENT.major() + ", got " + message.clientVersion.major());
        channel.unregisterFor(player);
        return;
      }

      // client is compatible, send the server's version and register the client
      context.execute(() -> {
        S2CMessages.HANDSHAKE_RESPONSE.sendTo(player,
            new S2CHandshakeResponse(ProtocolVersion.CURRENT, ShulkerBoxTooltip.config));
        ServerNetworking.addClient(player, message.clientVersion);
      });
    }

    @Override
    public void onRegister(MessageContext<C2SHandshakeStart> context) {
      if (context.getReceivingSide() == MessageContext.Side.CLIENT)
        C2SMessages.attemptHandshake();
    }
  }
}
