package com.misterpemodder.shulkerboxtooltip.api;

import com.misterpemodder.shulkerboxtooltip.impl.PreviewContextImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @since 2.0.0
 */
public interface PreviewContext {
  /**
   * Creates a preview context with an item stack.
   * 
   * @param stack The stack.
   * @return The created preview context
   * @since 2.0.0
   */
  static PreviewContext of(ItemStack stack) {
    return new PreviewContextImpl(stack.copy(), null);
  }

  /**
   * Creates a preview context with an item stack and an owner.
   * 
   * @param stack The stack.
   * @param owner The owner, may be null.
   * @return The created preview context
   * @since 2.0.0
   */
  static PreviewContext of(ItemStack stack, @Nullable PlayerEntity owner) {
    return new PreviewContextImpl(stack.copy(), owner);
  }

  /**
   * @return The item stack.
   * @since 2.0.0
   */
  ItemStack getStack();

  /**
   * @return The owner of this item stack, may be null.
   * @since 2.0.0
   */
  @Nullable
  PlayerEntity getOwner();
}
