package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Client-side cache for ender chest contents.
 * <p>
 * Stores ender chest inventories per world/server so that previews can be shown
 * even when server integration is unavailable.
 */
@Environment(EnvType.CLIENT)
public final class EnderChestCache {
  public static final EnderChestCache INSTANCE = new EnderChestCache();

  private static final String SP_CACHE_FILE = "shulkerboxtooltip_ender_chest_cache.dat";
  private static final String MP_CACHE_FILE = "shulkerboxtooltip_mp_ender_chest_cache.dat";
  private static final LevelResource DATA_DIR = LevelResource.DATA;
  /**
   * The cached ender chest inventory for the current session (current world/server).
   */
  @Nullable
  private ListTag cachedInventory;

  private EnderChestCache() {
  }

  /**
   * @return true if there is a cached inventory for the current world/server.
   */
  public boolean hasCachedInventory() {
    return this.cachedInventory != null;
  }

  /**
   * @return true if the cache exists and contains at least one non-empty stack.
   */
  public boolean hasCachedItems(HolderLookup.Provider registries) {
    return this.cachedInventory != null && this.hasItems(this.cachedInventory, registries);
  }

  /**
   * @return The cached ender chest items for the current world/server,
   * or an empty list if there is no cache entry.
   */
  @NotNull
  public List<ItemStack> getItems(HolderLookup.Provider registries) {
    if (this.cachedInventory == null)
      return Collections.emptyList();
    return this.deserializeItems(this.cachedInventory, registries);
  }

  /**
   * Updates the cached inventory and saves it to disk.
   */
  public void putItems(ListTag inventory) {
    this.cachedInventory = inventory;

    Minecraft mc = Minecraft.getInstance();

    if (mc.hasSingleplayerServer()) {
      this.saveSingleplayer(mc, inventory);
    } else {
      this.saveMultiplayer(mc, inventory);
    }
  }

  /**
   * Loads the cache from disk for the current world/server.
   * Should be called when joining a world.
   */
  public void loadFromDisk() {
    this.cachedInventory = null;

    Minecraft mc = Minecraft.getInstance();

    if (mc.hasSingleplayerServer()) {
      this.loadSingleplayer(mc);
    } else {
      this.loadMultiplayer(mc);
    }
  }

  @NotNull
  private List<ItemStack> deserializeItems(ListTag nbtInventory, HolderLookup.Provider registries) {
    var wrappedList = new CompoundTag();
    wrappedList.put("inv", nbtInventory);

    ValueInput valueInput = TagValueInput.create(ProblemReporter.DISCARDING, registries, wrappedList);
    ValueInput.TypedInputList<ItemStackWithSlot> decoded = valueInput.listOrEmpty("inv", ItemStackWithSlot.CODEC);

    // Find the required list size from the maximum slot index
    int size = 0;

    for (ItemStackWithSlot entry : decoded) {
      if (entry.slot() >= size)
        size = entry.slot() + 1;
    }

    List<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);

    for (var entry : decoded)
      items.set(entry.slot(), entry.stack().copy());
    return items;
  }

  private boolean hasItems(ListTag nbtInventory, HolderLookup.Provider registries) {
    var wrappedList = new CompoundTag();
    wrappedList.put("inv", nbtInventory);

    ValueInput valueInput = TagValueInput.create(ProblemReporter.DISCARDING, registries, wrappedList);
    ValueInput.TypedInputList<ItemStackWithSlot> decoded = valueInput.listOrEmpty("inv", ItemStackWithSlot.CODEC);

    for (ItemStackWithSlot entry : decoded) {
      if (!entry.stack().isEmpty()) {
        return true;
      }
    }
    return false;
  }

  private void loadSingleplayer(Minecraft mc) {
    Path cachePath = this.getSingleplayerCachePath(mc);

    if (cachePath == null || !Files.exists(cachePath))
      return;

    try {
      CompoundTag root = NbtIo.readCompressed(cachePath, NbtAccounter.unlimitedHeap());
      var inv = root.getList("inv");

      inv.ifPresent(listTag -> this.cachedInventory = listTag);
    } catch (IOException e) {
      ShulkerBoxTooltip.LOGGER.error("Failed to load ender chest cache", e);
    }
  }

  private void saveSingleplayer(Minecraft mc, ListTag inventory) {
    Path cachePath = this.getSingleplayerCachePath(mc);

    if (cachePath == null)
      return;

    try {
      Files.createDirectories(cachePath.getParent());

      CompoundTag root = new CompoundTag();

      root.put("inv", inventory);
      NbtIo.writeCompressed(root, cachePath);
    } catch (IOException e) {
      ShulkerBoxTooltip.LOGGER.error("Failed to save ender chest cache", e);
    }
  }

  private void loadMultiplayer(Minecraft mc) {
    var serverData = mc.getCurrentServer();

    if (serverData == null)
      return;

    Path cachePath = this.getMultiplayerCachePath();

    if (!Files.exists(cachePath))
      return;

    try {
      CompoundTag root = NbtIo.readCompressed(cachePath, NbtAccounter.unlimitedHeap());
      CompoundTag entries = root.getCompoundOrEmpty("entries");
      var inv = entries.getList(serverData.ip);

      inv.ifPresent(listTag -> this.cachedInventory = listTag);
    } catch (IOException e) {
      ShulkerBoxTooltip.LOGGER.error("Failed to load multiplayer ender chest cache", e);
    }
  }

  private void saveMultiplayer(Minecraft mc, ListTag inventory) {
    var serverData = mc.getCurrentServer();

    if (serverData == null)
      return;

    Path cachePath = this.getMultiplayerCachePath();

    try {
      CompoundTag root;

      if (Files.exists(cachePath)) {
        root = NbtIo.readCompressed(cachePath, NbtAccounter.unlimitedHeap());
      } else {
        root = new CompoundTag();
      }

      CompoundTag entries = root.getCompoundOrEmpty("entries");

      entries.put(serverData.ip, inventory);
      root.put("entries", entries);

      Files.createDirectories(cachePath.getParent());
      NbtIo.writeCompressed(root, cachePath);
    } catch (IOException e) {
      ShulkerBoxTooltip.LOGGER.error("Failed to save multiplayer ender chest cache", e);
    }
  }

  @Nullable
  private Path getSingleplayerCachePath(Minecraft mc) {
    IntegratedServer server = mc.getSingleplayerServer();

    if (server == null)
      return null;
    return server.getWorldPath(DATA_DIR).resolve(SP_CACHE_FILE);
  }

  @NotNull
  private Path getMultiplayerCachePath() {
    return ShulkerBoxTooltip.getConfigDir().resolve(MP_CACHE_FILE);
  }
}
