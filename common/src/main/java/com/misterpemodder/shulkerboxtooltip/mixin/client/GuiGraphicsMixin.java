package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsMixin implements GuiGraphicsExtensions {
  @Unique
  private int shulkerBoxTooltip$tooltipTopX = 0;
  @Unique
  private int shulkerBoxTooltip$tooltipTopY = 0;
  @Unique
  private int shulkerBoxTooltip$mouseX = 0;
  @Unique
  private int shulkerBoxTooltip$mouseY = 0;

  @Intrinsic
  public void setTooltipTopXPosition(int topX) {
    this.shulkerBoxTooltip$tooltipTopX = topX;
  }

  @Override
  @Intrinsic
  public int getTooltipTopXPosition() {
    return this.shulkerBoxTooltip$tooltipTopX;
  }

  @Intrinsic
  public void setTooltipTopYPosition(int topY) {
    this.shulkerBoxTooltip$tooltipTopY = topY;
  }

  @Override
  @Intrinsic
  public int getTooltipTopYPosition() {
    return this.shulkerBoxTooltip$tooltipTopY;
  }

  @Override
  @Intrinsic
  public void setMouseX(int mouseX) {
    this.shulkerBoxTooltip$mouseX = mouseX;
  }

  @Override
  @Intrinsic
  public int getMouseX() {
    return this.shulkerBoxTooltip$mouseX;
  }

  @Override
  @Intrinsic
  public void setMouseY(int mouseY) {
    this.shulkerBoxTooltip$mouseY = mouseY;
  }

  @Override
  @Intrinsic
  public int getMouseY() {
    return this.shulkerBoxTooltip$mouseY;
  }

  @Accessor
  @Nullable
  public abstract Runnable getDeferredTooltip();

  @Accessor
  public abstract void setDeferredTooltip(@Nullable Runnable deferredTooltip);
}
