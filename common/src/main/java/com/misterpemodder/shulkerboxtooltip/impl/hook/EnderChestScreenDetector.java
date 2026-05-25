package com.misterpemodder.shulkerboxtooltip.impl.hook;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.provider.EnderChestCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Detects when the player closes an ender chest screen and captures the contents
 * into the {@link EnderChestCache}.
 */
@Environment(EnvType.CLIENT)
public final class EnderChestScreenDetector {
  public static final EnderChestScreenDetector INSTANCE = new EnderChestScreenDetector();

  private static final Component ENDER_CHEST_TITLE = Component.translatable("container.enderchest");

  private EnderChestScreenDetector() {
  }

  /**
   * Called when a screen is removed. If it is an ender chest screen, captures the contents
   * into the cache and saves to disk.
   */
  public void onScreenClose(Screen screen) {
    if (!this.isEnderChestScreen(screen))
      return;

    Minecraft mc = Minecraft.getInstance();

    if (mc.player == null)
      return;

    var containerScreen = (AbstractContainerScreen<?>) screen;
    ListTag nbtInventory = this.captureFromMenu(containerScreen.getMenu(), mc.player.registryAccess());

    EnderChestCache.INSTANCE.putItems(nbtInventory);
  }

  /**
   * Checks whether the given screen is an ender chest container screen.
   */
  private boolean isEnderChestScreen(Screen screen) {
    ShulkerBoxTooltip.LOGGER.info("checking EC...");
    if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
      ShulkerBoxTooltip.LOGGER.info("Not ASC");
      return false;
    }

    try {
      if (containerScreen.getMenu().getType() != MenuType.GENERIC_9x3) {
        ShulkerBoxTooltip.LOGGER.info("Not Generic 9x3");
        return false;
      }
    } catch (UnsupportedOperationException e) {
      // AbstractContainerMenu.getType() throws if the menu type is not set
      ShulkerBoxTooltip.LOGGER.error("Bad menu", e);
      return false;
    }

    if (!screen.getTitle().getString().equals(ENDER_CHEST_TITLE.getString())) {
      ShulkerBoxTooltip.LOGGER.info("wrong title, expected " + ENDER_CHEST_TITLE.getString() + ", got " + screen.getTitle().getString());
    }

    return screen.getTitle().getString().equals(ENDER_CHEST_TITLE.getString());
  }

  /**
   * Captures the ender chest contents from a container menu.
   *
   * @return the serialized inventory as a ListTag (empty ListTag if the ender chest is empty)
   */
  @NotNull
  private ListTag captureFromMenu(AbstractContainerMenu menu, HolderLookup.Provider registries) {
    Container container = menu.slots.getFirst().container;

    var valueOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
    var list = valueOutput.list("inv", ItemStackWithSlot.CODEC);
    int slot = 0;

    for (ItemStack stack : container) {
      if (!stack.isEmpty())
        list.add(new ItemStackWithSlot(slot, stack));
      slot++;
    }
    return valueOutput.buildResult().getListOrEmpty("inv");
  }
}
