package com.misterpemodder.shulkerboxtooltip.impl.network.forge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.RegistrationChangeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class ChannelListener {
  private static final Supplier<String> DUMMY_VERSION = () -> "1";
  private static final Predicate<String> MATCH_ALL = v -> true;
  private static final Map<Identifier, ChannelListener> INSTANCES = new HashMap<>();

  public ServerNetworking.PacketReceiver c2sPacketReceiver;
  public ServerNetworking.RegistrationChangeListener c2sRegChangeListener;

  @OnlyIn(Dist.CLIENT)
  public ClientNetworking.PacketReceiver s2cPacketReceiver;
  @OnlyIn(Dist.CLIENT)
  public ClientNetworking.RegistrationChangeListener s2cRegChangeListener;

  private ChannelListener(EventNetworkChannel eventChannel) {
    eventChannel.addListener(this::onServerEvent);
    if (ShulkerBoxTooltip.isClient())
      eventChannel.addListener(this::onClientEvent);
  }

  public static ChannelListener get(Identifier channelId) {
    return INSTANCES.computeIfAbsent(channelId,
        id -> new ChannelListener(NetworkRegistry.newEventChannel(id, DUMMY_VERSION, MATCH_ALL, MATCH_ALL)));
  }

  private static ServerPlayerEntity sender(NetworkEvent event) {
    return event.getSource().get().getSender();
  }

  private static RegistrationChangeType registrationChangeType(NetworkEvent.ChannelRegistrationChangeEvent event) {
    return switch (event.getRegistrationChangeType()) {
      case REGISTER -> RegistrationChangeType.REGISTER;
      case UNREGISTER -> RegistrationChangeType.UNREGISTER;
    };
  }

  private void onServerEvent(NetworkEvent event) {
    if (event instanceof NetworkEvent.ClientCustomPayloadEvent customPayloadEvent && this.c2sPacketReceiver != null) {
      this.c2sPacketReceiver.handle(sender(customPayloadEvent), customPayloadEvent.getPayload());
      customPayloadEvent.getSource().get().setPacketHandled(true);
    } else if (event instanceof NetworkEvent.ChannelRegistrationChangeEvent regEvent
        && this.c2sRegChangeListener != null) {
      this.c2sRegChangeListener.onRegistrationChange(sender(regEvent), registrationChangeType(regEvent));
    }
  }

  @OnlyIn(Dist.CLIENT)
  private void onClientEvent(NetworkEvent event) {
    if (event instanceof NetworkEvent.ServerCustomPayloadEvent customPayloadEvent && this.s2cPacketReceiver != null) {
      this.s2cPacketReceiver.handle(customPayloadEvent.getPayload());
      customPayloadEvent.getSource().get().setPacketHandled(true);
    } else if (event instanceof NetworkEvent.ChannelRegistrationChangeEvent regEvent
        && this.s2cRegChangeListener != null) {
      this.s2cRegChangeListener.onRegistrationChange(registrationChangeType(regEvent));
    }
  }
}
