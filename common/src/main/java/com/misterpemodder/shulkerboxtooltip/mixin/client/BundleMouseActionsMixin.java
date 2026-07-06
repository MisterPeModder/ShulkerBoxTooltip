package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.config.PreviewConfiguration;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.util.MergedItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.BundleMouseActions;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundSelectBundleItemPacket;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(BundleMouseActions.class)
public class BundleMouseActionsMixin {
  @Final
  @Shadow
  private Minecraft minecraft;

  @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BundleItem;getNumberOfItemsToShow("
                                                     + "Lnet/minecraft/world/item/ItemStack;)I"), method = "onMouseScrolled(DDILnet/minecraft/world/item/ItemStack;)Z")
  private int changeOnScrollAmountOfShownItems(ItemStack stack, Operation<Integer> original,
      @Share("context") LocalRef<PreviewContext> contextRef, @Share("provider") LocalRef<PreviewProvider> providerRef) {
    PreviewContext context = PreviewContext.builder(stack).withOwner(this.minecraft.player).build();
    PreviewProvider provider = ShulkerBoxTooltipApi.getProviderIfPreviewAvailable(context);

    contextRef.set(context);
    providerRef.set(provider);

    return this.shulkerBoxTooltip$getActualNumberOfItemsToShow(context, provider, original);
  }

  @WrapOperation(at = @At(value = "INVOKE", target =
      "Lnet/minecraft/client/gui/BundleMouseActions;toggleSelectedBundleItem("
      + "Lnet/minecraft/world/item/ItemStack;II)V"), method = "onMouseScrolled(DDILnet/minecraft/world/item/ItemStack;)Z")
  private void toggleSelectedItemsBypassLimit(BundleMouseActions instance, ItemStack bundleItem, int slotIndex,
      int selectedItem, Operation<Void> original, @Share("context") LocalRef<PreviewContext> contextRef,
      @Share("provider") LocalRef<PreviewProvider> providerRef) {
    PreviewContext context = contextRef.get();
    PreviewProvider provider = providerRef.get();

    ClientPacketListener connection = this.minecraft.getConnection();

    if (connection != null && selectedItem < this.shulkerBoxTooltip$getActualNumberOfItemsToShow(context, provider,
        (args) -> BundleItem.getNumberOfItemsToShow((ItemStack) (args[0])))) {
      BundleItem.toggleSelectedItem(bundleItem, selectedItem);
      connection.send(new ServerboundSelectBundleItemPacket(slotIndex, selectedItem));
    } else {
      original.call(instance, bundleItem, slotIndex, selectedItem);
    }
  }

  @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ScrollWheelHandler;"
                                                     + "getNextScrollWheelSelection(DII)I"), method = "onMouseScrolled(DDILnet/minecraft/world/item/ItemStack;)Z")
  private int getNextScrollWheelSelectionInPreview(double wheel, int currentSelected, int limit,
      Operation<Integer> original, @Share("context") LocalRef<PreviewContext> contextRef,
      @Share("provider") LocalRef<PreviewProvider> providerRef) {
    PreviewContext context = contextRef.get();
    PreviewProvider provider = providerRef.get();

    if (context == null || provider == null)
      return original.call(wheel, currentSelected, limit);

    // Scrolling only needs to be changed on COMPACT mode, where order and amount of slots may not reflect vanilla
    if (ShulkerBoxTooltipApi.getCurrentPreviewType(provider.isFullPreviewAvailable(context)) != PreviewType.COMPACT)
      return original.call(wheel, currentSelected, limit);

    PreviewConfiguration config = context.config();

    List<ItemStack> inv = provider.getInventory(context);
    List<MergedItemStack> mergedInv = MergedItemStack.mergeInventory(inv, limit, config.itemStackMergingStrategy(),
        config.compactPreviewOrder().toComparator());

    // virtual to real slot indices
    int[] virtualToReal = new int[limit];
    int virtualSlotCount = 0;

    // Put all sorted items of the compact preview next to each other with no gaps
    for (MergedItemStack mergedStack : mergedInv) {
      for (int i = 0; i < limit; ++i) {
        // getSubStack(i) keys sub-stacks by their original inventory slot, so i is the real index
        if (!mergedStack.getSubStack(i).isEmpty()) {
          virtualToReal[virtualSlotCount++] = i;
        }
      }
    }

    // Index of *next* selected stack into compact preview sorted items
    int virtualUserSelectedItem = 0;

    if (currentSelected >= 0 && currentSelected < virtualSlotCount) {
      // Index of *current* selected stack into compact preview sorted items
      int virtualCurrentSelected = shulkerboxtooltip$indexOf(virtualToReal, virtualSlotCount, currentSelected);

      if (virtualCurrentSelected >= 0)
        virtualUserSelectedItem = ScrollWheelHandler.getNextScrollWheelSelection(wheel, virtualCurrentSelected,
            virtualSlotCount);
    }

    // Real bundle inventory index of the next selected stack
    return virtualToReal[virtualUserSelectedItem];
  }

  @Unique
  private static int shulkerboxtooltip$indexOf(int[] values, int length, int value) {
    for (int i = 0; i < length; ++i) {
      if (values[i] == value) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Allows the user to select items past the vanilla limit when a compact or full preview from SBT is shown.
   */
  @Unique
  private int shulkerBoxTooltip$getActualNumberOfItemsToShow(PreviewContext context, @Nullable PreviewProvider provider,
      Operation<Integer> original) {
    if (provider == null)
      return original.call(context.stack());

    BundleContents contents = context.stack().getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
    return contents.size();
  }
}
