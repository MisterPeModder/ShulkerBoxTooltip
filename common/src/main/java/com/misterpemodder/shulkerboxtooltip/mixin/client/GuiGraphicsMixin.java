package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements GuiGraphicsExtensions {
  @Unique
  private int tooltipTopY = 0;
  @Unique
  private int mouseX = 0;
  @Unique
  private int mouseY = 0;

  @Intrinsic
  public void setTooltipTopYPosition(int topY) {
    this.tooltipTopY = topY;
  }

  @Override
  @Intrinsic
  public int getTooltipTopYPosition() {
    return this.tooltipTopY;
  }

  @Override
  @Intrinsic
  public void setMouseX(int mouseX) {
    this.mouseX = mouseX;
  }

  @Override
  @Intrinsic
  public int getMouseX() {
    return this.mouseX;
  }

  @Override
  @Intrinsic
  public void setMouseY(int mouseY) {
    this.mouseY = mouseY;
  }

  @Override
  @Intrinsic
  public int getMouseY() {
    return this.mouseY;
  }

  @Accessor
  @Nullable
  public abstract Runnable getDeferredTooltip();

  @Accessor
  public abstract void setDeferredTooltip(@Nullable Runnable deferredTooltip);
}
