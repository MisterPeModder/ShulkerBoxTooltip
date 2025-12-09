package com.misterpemodder.shulkerboxtooltip.impl.network.forge;

import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.C2SChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.context.C2SMessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.PacketDistributor;

public class ForgeC2SChannel<T> extends ForgeChannel<T> implements C2SChannel<T> {
  public ForgeC2SChannel(Identifier id, MessageType<T> type) {
    super(id, type);
  }

  @Override
  public void registerFor(ServerPlayer player) {
    // Forge does not support dynamic channel registration
  }

  @Override
  public void unregisterFor(ServerPlayer player) {
    // Forge does not support dynamic channel registration
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void sendToServer(T message) {
    this.innerChannel.send(new Payload<>(this.id, message), PacketDistributor.SERVER.noArg());
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public boolean canSendToServer() {
    ClientPacketListener listener = Minecraft.getInstance().getConnection();
    return listener != null && this.innerChannel.isRemotePresent(listener.getConnection());
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void onDisconnect() {
  }

  @Override
  protected void onReceive(Payload<T> payload, CustomPayloadEvent.Context context) {
    if (context.isServerSide()) {
      var listener = (ServerGamePacketListenerImpl) context.getConnection().getPacketListener();
      this.type.onReceive(payload.value(), new C2SMessageContext<>(listener.getPlayer(), this));
    }
    context.setPacketHandled(true);
  }
}
