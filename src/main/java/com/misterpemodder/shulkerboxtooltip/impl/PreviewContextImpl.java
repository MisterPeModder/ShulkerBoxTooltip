package com.misterpemodder.shulkerboxtooltip.impl;

import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PreviewContextImpl implements PreviewContext {
  protected final ItemStack stack;
  protected final PlayerEntity owner;

  public PreviewContextImpl(ItemStack stack, @Nullable PlayerEntity owner) {
    this.stack = stack;
    this.owner = owner;
  }

  @Override
  public ItemStack getStack() {
    return this.stack;
  }

  @Override
  public PlayerEntity getOwner() {
    return this.owner;
  }
}
