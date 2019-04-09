package com.misterpemodder.shulkerboxtooltip;

import java.util.List;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.Configuration.ShulkerBoxTooltipType;
import com.misterpemodder.shulkerboxtooltip.hook.ShulkerPreviewPosGetter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public final class ShulkerBoxTooltip implements ClientModInitializer {
  private static ShulkerBoxPreviewRenderer previewRenderer;

  @Override
  public void onInitializeClient() {
    Configuration.initConfiguration();
  }

  /**
   * Modifies the shulker box tooltip.
   * 
   * @param stack    The shulker box item stack
   * @param tooltip  The list to put the tooltip in.
   * @param compound The stack NBT data.
   * @return true to cancel vanilla tooltip code, false otherwise.
   */
  public static boolean buildShulkerBoxTooltip(ItemStack stack, List<TextComponent> tooltip,
      @Nullable CompoundTag compound) {
    if (Configuration.getTooltipType() == ShulkerBoxTooltipType.NONE)
      return true;
    if (Configuration.getTooltipType() == ShulkerBoxTooltipType.VANILLA)
      return false;
    if (compound == null) {
      tooltip.add(
          new TranslatableTextComponent("container.shulkerBox.empty").applyFormat(TextFormat.GRAY));
    } else if (compound.containsKey("LootTable", NbtType.STRING)) {
      tooltip.add(new StringTextComponent(TextFormat.GRAY + "???????"));
    } else if (compound.containsKey("Items", NbtType.LIST)) {
      ListTag list = compound.getList("Items", NbtType.COMPOUND);
      if (list.size() > 0) {
        tooltip.add(new TranslatableTextComponent("container.shulkerBox.contains", list.size())
            .applyFormat(TextFormat.GRAY));
        if (Screen.hasShiftDown() && Screen.hasAltDown())
          return true;
        boolean noPreview = getCurrentPreviewType() == ShulkerBoxPreviewType.NO_PREVIEW;
        tooltip.add(new StringTextComponent(noPreview ? "Shift: " : "Alt+Shift: ")
            .applyFormat(TextFormat.GOLD)
            .append(new TranslatableTextComponent(
                "container.shulkerBox." + (noPreview ? "viewContents" : "viewFullContents"))
                    .applyFormat(TextFormat.WHITE)));
      } else {
        tooltip.add(new TranslatableTextComponent("container.shulkerBox.empty")
            .applyFormat(TextFormat.GRAY));
      }
    }
    return true;
  }

  /**
   * @return The shulker box tooltip type depending of which keys are pressed.
   */
  public static ShulkerBoxPreviewType getCurrentPreviewType() {
    if (Configuration.areModesSwapped()) {
      if (Screen.hasShiftDown())
        return Screen.hasAltDown() ? ShulkerBoxPreviewType.COMPACT : ShulkerBoxPreviewType.FULL;
    } else {
      if (Screen.hasShiftDown())
        return Screen.hasAltDown() ? ShulkerBoxPreviewType.FULL : ShulkerBoxPreviewType.COMPACT;
    }
    return ShulkerBoxPreviewType.NO_PREVIEW;
  }

  /**
   * Should the shulker box previex be drawn? Also checks if the passed {@link ItemStack} is a
   * shulker box.
   * 
   * @param stack The stack to check.
   * @return true if the preview should be drawn.
   */
  public static boolean hasShulkerBoxPreview(ItemStack stack) {
    return Configuration.isPreviewEnabled()
        && getCurrentPreviewType() != ShulkerBoxPreviewType.NO_PREVIEW
        && Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock;
  }

  public static void drawShulkerBoxPreview(Screen screen, ItemStack stack, int mouseX, int mouseY) {
    if (previewRenderer == null)
      previewRenderer = new ShulkerBoxPreviewRenderer();
    previewRenderer.setShulkerStack(stack);
    previewRenderer.setPreviewType(getCurrentPreviewType());
    int x = Math.min(((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getStartX() - 1,
        screen.width - previewRenderer.getWidth());
    int y = ((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getBottomY() + 1;
    int h = previewRenderer.getHeight();
    if (Configuration.isPreviewLocked() || y + h > screen.height)
      y = ((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getTopY() - h;
    previewRenderer.draw(x, y);
  }
}
