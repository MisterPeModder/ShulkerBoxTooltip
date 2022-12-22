package com.misterpemodder.shulkerboxtooltip.impl.network.message;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.ProtocolVersion;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import net.minecraft.network.PacketByteBuf;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Response to a client's handshake attempt.
 *
 * @param serverVersion The server's protocol version.
 * @param config        The server's configuration.
 */
public record S2CHandshakeResponse(@Nullable ProtocolVersion serverVersion, Configuration config) {
  public static class Type implements MessageType<S2CHandshakeResponse> {
    @Override
    public void encode(S2CHandshakeResponse message, PacketByteBuf buf) {
      Objects.requireNonNull(message.serverVersion).writeToPacketBuf(buf);
      ConfigurationHandler.writeToPacketBuf(message.config, buf);
    }

    @Override
    public S2CHandshakeResponse decode(PacketByteBuf buf) {
      ProtocolVersion serverVersion = ProtocolVersion.readFromPacketBuf(buf);
      Configuration config = ConfigurationHandler.copyOf(ShulkerBoxTooltip.config);

      if (serverVersion != null && serverVersion.major() == ProtocolVersion.CURRENT.major()) {
        try {
          ConfigurationHandler.readFromPacketBuf(config, buf);
        } catch (RuntimeException e) {
          ShulkerBoxTooltip.LOGGER.error("failed to read server configuration", e);
        }
      }

      return new S2CHandshakeResponse(serverVersion, config);
    }

    @Override
    public void onReceive(S2CHandshakeResponse message, MessageContext<S2CHandshakeResponse> context) {
      ShulkerBoxTooltip.LOGGER.info("Handshake succeeded");
      if (message.serverVersion != null) {
        if (message.serverVersion.major() == ProtocolVersion.CURRENT.major()) {
          ShulkerBoxTooltip.LOGGER.info("Server protocol version: " + message.serverVersion);

          ClientNetworking.serverProtocolVersion = message.serverVersion;
          ShulkerBoxTooltip.config = message.config;
          S2CMessages.HANDSHAKE_RESPONSE.unregister();
          return;
        }
        ShulkerBoxTooltip.LOGGER.error("Incompatible server protocol version, expected "
                + ProtocolVersion.CURRENT.major() + ", got " + message.serverVersion.major());
      } else {
        ShulkerBoxTooltip.LOGGER.error("Could not read server protocol version");
      }
      S2CMessages.unregisterAll();
    }
  }
}
