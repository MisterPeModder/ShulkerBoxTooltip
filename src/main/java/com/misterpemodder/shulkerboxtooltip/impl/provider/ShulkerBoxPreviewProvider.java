package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.DyeColor;

public class ShulkerBoxPreviewProvider extends BlockEntityPreviewProvider {
  private static float[] SHULKER_BOX_COLOR = new float[] { 0.592f, 0.403f, 0.592f };

  public ShulkerBoxPreviewProvider() {
    super(27, true);
  }

  @Override
  public boolean showTooltipHints(PreviewContext context) {
    return true;
  }

  @Override
  public float[] getWindowColor(PreviewContext context) {
    DyeColor dye = ((ShulkerBoxBlock) Block.getBlockFromItem(context.getStack().getItem())).getColor();
    if (dye != null) {
      float[] components = dye.getColorComponents();
      return new float[] { Math.max(0.15f, components[0]), Math.max(0.15f, components[1]),
          Math.max(0.15f, components[2]) };
    } else {
      return SHULKER_BOX_COLOR;
    }
  }
}
