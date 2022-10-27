package com.misterpemodder.shulkerboxtooltip.api;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.config.PreviewConfiguration;
import com.misterpemodder.shulkerboxtooltip.impl.PreviewContextImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
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
  @Nonnull
  @Contract("_ -> new")
  static PreviewContext of(ItemStack stack) {
    return new PreviewContextImpl(stack.copy(), null, ShulkerBoxTooltip.config);
  }

  /**
   * Creates a preview context with an item stack and an owner.
   *
   * @param stack The stack.
   * @param owner The owner, may be null.
   * @return The created preview context
   * @since 2.0.0
   */
  @Nonnull
  @Contract("_, _ -> new")
  static PreviewContext of(ItemStack stack, @Nullable PlayerEntity owner) {
    return new PreviewContextImpl(stack.copy(), owner, ShulkerBoxTooltip.config);
  }

  /**
   * @return The item stack.
   * @since 3.1.0
   */
  @Nonnull
  ItemStack stack();

  /**
   * @return The owner of this item stack, may be null.
   * @since 3.1.0
   */
  @Nullable
  PlayerEntity owner();

  /**
   * @return the configuration in use for this context.
   * @since 3.3.0
   */
  @Nonnull
  PreviewConfiguration config();

  /**
   * @return The item stack.
   * @since 2.0.0
   * @deprecated Use {@link #stack()} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "4.0.0")
  default ItemStack getStack() {
    return this.stack();
  }

  /**
   * @return The owner of this item stack, may be null.
   * @since 2.0.0
   * @deprecated Use {@link #owner()} instead.
   */
  @Nullable
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "4.0.0")
  default PlayerEntity getOwner() {
    return this.owner();
  }
}
