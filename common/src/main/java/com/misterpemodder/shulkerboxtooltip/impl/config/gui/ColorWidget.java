package com.misterpemodder.shulkerboxtooltip.impl.config.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.IntSupplier;

@Environment(EnvType.CLIENT)
public class ColorWidget extends AbstractWidget {
  private static final WidgetSprites SPRITES = new WidgetSprites(
      ResourceLocation.withDefaultNamespace("widget/text_field"),
      ResourceLocation.withDefaultNamespace("widget/text_field_highlighted"));

  private final AbstractWidget neighbor;
  private final IntSupplier colorSupplier;

  public ColorWidget(Component label, AbstractWidget neighbor, IntSupplier colorSupplier) {
    super(0, 0, 18, 18, label);
    this.neighbor = neighbor;
    this.colorSupplier = colorSupplier;
    this.active = false;
  }

  @Override
  protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
    if (!this.visible)
      return;
    ResourceLocation resourceLocation = SPRITES.get(this.isActive(), this.neighbor.isFocused());
    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, resourceLocation, this.getX(), this.getY(), this.getWidth(),
        this.getHeight());
    guiGraphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.getWidth() - 1,
        this.getY() + this.getHeight() - 1, 0xFF000000 | this.colorSupplier.getAsInt());
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    // nothing to narrate
  }
}
