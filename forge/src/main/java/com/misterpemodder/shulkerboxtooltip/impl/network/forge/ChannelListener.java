package com.misterpemodder.shulkerboxtooltip.impl.network.forge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.RegistrationChangeType;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.ChannelRegistrationChangeEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.EventNetworkChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = ShulkerBoxTooltip.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
final class ChannelListener {
  private static final int DUMMY_VERSION = 1;
  private static final Channel.VersionTest MATCH_ALL = (status, version) -> true;
  private static final Map<Identifier, ChannelListener> INSTANCES = new HashMap<>();

  public ServerNetworking.PacketReceiver c2sPacketReceiver;
  public ServerNetworking.RegistrationChangeListener c2sRegChangeListener;

  @OnlyIn(Dist.CLIENT)
  public ClientNetworking.PacketReceiver s2cPacketReceiver;
  @OnlyIn(Dist.CLIENT)
  public ClientNetworking.RegistrationChangeListener s2cRegChangeListener;

  private ChannelListener(EventNetworkChannel eventChannel) {
    eventChannel.addListener(this::onServerEvent);
    DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> eventChannel.addListener(this::onClientEvent));
  }

  public static ChannelListener get(Identifier channelId) {
    return INSTANCES.computeIfAbsent(channelId, id -> new ChannelListener(ChannelBuilder.named(id)
        .networkProtocolVersion(DUMMY_VERSION)
        .serverAcceptedVersions(MATCH_ALL)
        .clientAcceptedVersions(MATCH_ALL)
        .eventNetworkChannel()));
  }

  private static RegistrationChangeType registrationChangeType(ChannelRegistrationChangeEvent event) {
    return switch (event.getType()) {
      case REGISTER -> RegistrationChangeType.REGISTER;
      case UNREGISTER -> RegistrationChangeType.UNREGISTER;
    };
  }

  private void onServerEvent(Event event) {
    if (event instanceof CustomPayloadEvent customPayloadEvent && this.c2sPacketReceiver != null) {
      this.c2sPacketReceiver.handle(customPayloadEvent.getSource().getSender(), customPayloadEvent.getPayload());
      customPayloadEvent.getSource().setPacketHandled(true);
    } else if (event instanceof ChannelRegistrationChangeEvent regEvent && this.c2sRegChangeListener != null) {
      PacketListener netHandler = regEvent.getSource().getPacketListener();

      if (netHandler instanceof ServerPlayNetworkHandler handler) {
        this.c2sRegChangeListener.onRegistrationChange(handler.player, registrationChangeType(regEvent));
      }
    }
  }

  @OnlyIn(Dist.CLIENT)
  private void onClientEvent(Event event) {
    if (event instanceof CustomPayloadEvent customPayloadEvent && this.s2cPacketReceiver != null) {
      this.s2cPacketReceiver.handle(customPayloadEvent.getPayload());
      customPayloadEvent.getSource().setPacketHandled(true);
    } else if (event instanceof ChannelRegistrationChangeEvent regEvent && this.s2cRegChangeListener != null) {
      this.s2cRegChangeListener.onRegistrationChange(registrationChangeType(regEvent));
    }
  }

  @SubscribeEvent
  public static void onChannelRegistrationChangeEvent(ChannelRegistrationChangeEvent event) {
    var listeners = event.getChannels().stream().map(INSTANCES::get).filter(Objects::nonNull);

    if (event.getSource().getSide() == NetworkSide.SERVERBOUND) {
      listeners.forEach(listener -> listener.onServerEvent(event));
    } else {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
          () -> () -> listeners.forEach(listener -> listener.onClientEvent(event)));
    }
  }
}
