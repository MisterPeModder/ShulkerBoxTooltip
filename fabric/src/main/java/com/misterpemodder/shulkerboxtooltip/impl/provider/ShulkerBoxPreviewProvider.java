package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;

import java.util.Collections;
import java.util.List;

public class ShulkerBoxPreviewProvider extends BlockEntityPreviewProvider {
  private static final float[] SHULKER_BOX_COLOR = new float[]{0.592f, 0.403f, 0.592f};

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

  @Override
  public List<Text> addTooltip(PreviewContext context) {
    ItemStack stack = context.getStack();
    NbtCompound compound = stack.getNbt();

    if (this.canUseLootTables && compound != null && compound.contains("BlockEntityTag", 10)) {
      NbtCompound blockEntityTag = compound.getCompound("BlockEntityTag");

      if (blockEntityTag != null && blockEntityTag.contains("LootTable", 8)
        && ShulkerBoxTooltip.config.tooltip.lootTableInfoType == Configuration.LootTableInfoType.HIDE) {
        Style style = Style.EMPTY.withColor(Formatting.GRAY);

        return Collections.singletonList(new LiteralText("???????").setStyle(style));
      }
    }
    return super.addTooltip(context);
  }
}
