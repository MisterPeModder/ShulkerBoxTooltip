package com.misterpemodder.shulkerboxtooltip;

import java.util.List;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.hook.ShulkerPreviewPosGetter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public final class ShulkerBoxTooltip {
  private static ShulkerBoxPreviewRenderer previewRenderer = new ShulkerBoxPreviewRenderer();

  /**
   * Modifies the shulker box tooltip.
   * 
   * @return true to cancel vanilla tooltip code, false otherwise.
   */
  public static void buildShulkerBoxTooltip(ItemStack stack, @Nullable BlockView view,
      List<TextComponent> tooltip, TooltipContext options, @Nullable CompoundTag compound) {
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
        if (!Screen.isShiftPressed())
          tooltip.add(new StringTextComponent("Shift: ").applyFormat(TextFormat.GOLD)
              .append(new TranslatableTextComponent("container.shulkerBox.viewContents")
                  .applyFormat(TextFormat.WHITE)));
        else if (!Screen.isAltPressed())
          tooltip.add(new StringTextComponent("Alt+Shift: ").applyFormat(TextFormat.GOLD)
              .append(new TranslatableTextComponent("container.shulkerBox.viewFullContents")
                  .applyFormat(TextFormat.WHITE)));
      } else {
        tooltip.add(new TranslatableTextComponent("container.shulkerBox.empty")
            .applyFormat(TextFormat.GRAY));
      }
    }
    return;
  }

  /**
   * Should the shulker box previex be drawn? Also checks if the passed {@link ItemStack} is a
   * shulker box.
   * 
   * @param stack The stack to check.
   * @return true if the preview should be drawn.
   */
  public static boolean hasShulkerBoxPreview(ItemStack stack) {
    return Screen.isShiftPressed()
        && Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock;
  }

  public static void drawShulkerBoxPreview(Screen screen, ItemStack stack, int mouseX, int mouseY) {
    previewRenderer.setShulkerStack(stack);
    previewRenderer.setCompactStacksRender(!Screen.isAltPressed());
    int x = Math.min(((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getStartX() - 1,
        screen.width - previewRenderer.getWidth());
    int y = ((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getStartY() + 1;
    int h = previewRenderer.getHeight();
    if (y + h > screen.height)
      y = mouseY - 15 - h;
    previewRenderer.draw(x, y);
  }
}
