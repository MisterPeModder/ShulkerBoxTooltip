package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import net.minecraft.client.gui.BundleMouseActions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BundleMouseActions.class)
public class BundleMouseActionsMixin {
  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BundleItem;getNumberOfItemsToShow("
                                                + "Lnet/minecraft/world/item/ItemStack;)I"), method = "onMouseScrolled(DDILnet/minecraft/world/item/ItemStack;)Z")
  private int changeOnScrollAmountOfShownItems(ItemStack stack) {
    return shulkerBoxTooltip$getActualNumberOfItemsToShow(stack);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BundleItem;getNumberOfItemsToShow("
                                                + "Lnet/minecraft/world/item/ItemStack;)I"), method = "toggleSelectedBundleItem(Lnet/minecraft/world/item/ItemStack;II)V")
  private int changeToggleSelectedAmountOfShownItems(ItemStack stack) {
    return shulkerBoxTooltip$getActualNumberOfItemsToShow(stack);
  }

  /**
   * Allows the user to select items past the vanilla limit when a compact or full preview from SBT is shown.
   */
  @Unique
  private static int shulkerBoxTooltip$getActualNumberOfItemsToShow(ItemStack stack) {
    PreviewContext context = PreviewContext.builder(stack).withOwner(
        ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player).build();

    if (!ShulkerBoxTooltipApi.isPreviewAvailable(context))
      return BundleItem.getNumberOfItemsToShow(stack);

    BundleContents contents = stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
    return contents.size();
  }
}
