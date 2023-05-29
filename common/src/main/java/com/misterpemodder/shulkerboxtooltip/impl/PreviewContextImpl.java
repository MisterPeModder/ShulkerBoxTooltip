package com.misterpemodder.shulkerboxtooltip.impl;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public record PreviewContextImpl(ItemStack stack, PlayerEntity owner, Configuration config)
    implements PreviewContext {
  public PreviewContextImpl(ItemStack stack, @Nullable PlayerEntity owner, Configuration config) {
    this.stack = stack;
    this.owner = owner;
    this.config = config;
  }
}
