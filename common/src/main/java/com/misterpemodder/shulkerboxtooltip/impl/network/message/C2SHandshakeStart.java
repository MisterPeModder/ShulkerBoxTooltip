package com.misterpemodder.shulkerboxtooltip.impl.network.message;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.ProtocolVersion;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.C2SChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Initiates a handshake with the server.
 *
 * Sent by clients as soon as the server registers the capability to handle it.
 *
 * @param clientVersion The protocol version of the client.
 */
public record C2SHandshakeStart(ProtocolVersion clientVersion) {
  public static class Type implements MessageType<C2SHandshakeStart> {

    @Override
    public void encode(C2SHandshakeStart message, PacketByteBuf buf) {
      message.clientVersion.writeToPacketBuf(buf);
    }

    @Override
    public C2SHandshakeStart decode(PacketByteBuf buf) {
      return new C2SHandshakeStart(ProtocolVersion.readFromPacketBuf(buf));
    }

    @Override
    public void onReceive(C2SHandshakeStart message, MessageContext<C2SHandshakeStart> context) {
      var player = (ServerPlayerEntity) context.getPlayer();
      var channel = (C2SChannel<C2SHandshakeStart>) context.getChannel();

      if (message.clientVersion == null) {
        ShulkerBoxTooltip.LOGGER.error(
            "[" + ShulkerBoxTooltip.MOD_NAME + "] " + player.getEntityName() + ": received invalid handshake packet");
        channel.unregisterFor(player);
        return;
      }

      // compatibility check
      ShulkerBoxTooltip.LOGGER.info(
          "[" + ShulkerBoxTooltip.MOD_NAME + "] " + player.getEntityName() + ": protocol version: "
              + message.clientVersion);
      if (message.clientVersion.major() != ProtocolVersion.CURRENT.major()) {
        ShulkerBoxTooltip.LOGGER.error("[" + ShulkerBoxTooltip.MOD_NAME + "] " + player.getEntityName()
            + ": incompatible client protocol version, expected " + ProtocolVersion.CURRENT.major() + ", got "
            + message.clientVersion.major());
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
      if (context.getSide() == MessageContext.Side.CLIENT && ShulkerBoxTooltip.config.preview.serverIntegration
          && ClientNetworking.serverProtocolVersion == null && C2SMessages.HANDSHAKE_START.canSendToServer()) {
        ShulkerBoxTooltip.LOGGER.info(
            "[" + ShulkerBoxTooltip.MOD_NAME + "] Server integration enabled, attempting handshake...");
        C2SMessages.HANDSHAKE_START.sendToServer(new C2SHandshakeStart(ProtocolVersion.CURRENT));
      }
    }
  }
}
