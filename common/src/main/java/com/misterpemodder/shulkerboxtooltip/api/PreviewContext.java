package com.misterpemodder.shulkerboxtooltip.api;

import com.misterpemodder.shulkerboxtooltip.impl.PreviewContextImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides information for item previews, such as the item stack and player that owns the stack (if present).
 *
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
  @Nonnull
  @Contract("_, _ -> new")
  static PreviewContext of(ItemStack stack, @Nullable PlayerEntity owner) {
    return new PreviewContextImpl(stack.copy(), owner);
  }

  /**
   * Gets the item stack associated with this context.
   *
   * @return The item stack.
   * @since 3.1.0
   */
  @Nonnull
  ItemStack stack();

  /**
   * Gets the player associated with this context, or null if it does not exist.
   *
   * @return The owner of this item stack, may be null.
   * @since 3.1.0
   */
  @Nullable
  PlayerEntity owner();

  /**
   * @return The item stack.
   * @since 2.0.0
   * @deprecated Use {@link #stack()} instead.
   */
  @Deprecated(forRemoval = true)
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
  @Deprecated(forRemoval = true)
  @ApiStatus.ScheduledForRemoval(inVersion = "4.0.0")
  default PlayerEntity getOwner() {
    return this.owner();
  }
}
