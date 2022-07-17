package com.misterpemodder.shulkerboxtooltip.impl;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public record PreviewContextImpl(ItemStack stack, PlayerEntity owner) implements PreviewContext {
  public PreviewContextImpl(ItemStack stack, @Nullable PlayerEntity owner) {
    this.stack = stack;
    this.owner = owner;
  }
}
