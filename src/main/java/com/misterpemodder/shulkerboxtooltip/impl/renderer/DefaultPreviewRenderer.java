package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.provider.EmptyPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.CompactPreviewTagBehavior;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.Theme;
import com.misterpemodder.shulkerboxtooltip.impl.util.MergedItemStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DefaultPreviewRenderer implements PreviewRenderer {
  private static final Identifier DEFAULT_TEXTURE_LIGHT =
      new Identifier("shulkerboxtooltip", "textures/gui/shulker_box_tooltip.png");
  private static final Identifier DEFAULT_TEXTURE_DARK =
      new Identifier("shulkerboxtooltip", "textures/gui/shulker_box_tooltip_dark.png");
  public static final DefaultPreviewRenderer INSTANCE = new DefaultPreviewRenderer();

  private List<MergedItemStack> items;
  private PreviewContext previewContext;
  private PreviewType previewType;
  private PreviewProvider provider;
  private int maxRowSize;
  private int compactMaxRowSize;
  private Identifier textureOverride;

  private DefaultPreviewRenderer() {
    this.items = new ArrayList<>();
    this.previewType = PreviewType.FULL;
    this.maxRowSize = 9;
    this.setPreview(PreviewContext.of(new ItemStack(Items.AIR)), EmptyPreviewProvider.INSTANCE);
  }

  @Override
  public void setPreview(PreviewContext context, PreviewProvider provider) {
    List<ItemStack> inventory = provider.getInventory(context);
    int rowSize = provider.getMaxRowSize(context);

    this.compactMaxRowSize = ShulkerBoxTooltip.config.main.defaultMaxRowSize;
    if (this.compactMaxRowSize <= 0)
      this.compactMaxRowSize = 9;
    if (rowSize <= 0)
      rowSize = this.compactMaxRowSize;
    this.maxRowSize = rowSize <= 0 ? 9 : rowSize;
    this.textureOverride = provider.getTextureOverride(context);
    this.provider = provider;
    this.items = MergedItemStack.mergeInventory(inventory, provider.getInventoryMaxSize(context),
        ShulkerBoxTooltip.config.main.compactPreviewTagBehavior != CompactPreviewTagBehavior.SEPARATE);
    this.previewContext = context;
  }

  public int getMaxRowSize() {
    return this.previewType == PreviewType.COMPACT ? this.compactMaxRowSize : this.maxRowSize;
  }

  @Override
  public void setPreviewType(PreviewType type) {
    this.previewType = type;
  }

  @Override
  public int getWidth() {
    return 14 + Math.min(this.getMaxRowSize(), this.getInvSize()) * 18;
  }

  @Override
  public int getHeight() {
    return 14 + (int) Math.ceil(this.getInvSize() / (double) this.getMaxRowSize()) * 18;
  }

  private int getInvSize() {
    return this.previewType == PreviewType.COMPACT ? Math.max(1, this.items.size())
        : this.provider.getInventoryMaxSize(this.previewContext);
  }

  /**
   * Sets the color of the preview window.
   * 
   * @return the color that was used.
   */
  private float[] setColor() {
    float[] color;

    if (ShulkerBoxTooltip.config.main.coloredPreview) {
      color = this.provider.getWindowColor(this.previewContext);
      if (color == null || color.length < 3) {
        color = PreviewProvider.DEFAULT_COLOR;
      }
    } else {
      color = PreviewProvider.DEFAULT_COLOR;
    }
    RenderSystem.setShaderColor(color[0], color[1], color[2], 1.0f);
    return color;
  }

  /**
   * Sets the texture to be used.
   * 
   * @param color An array of three floats.
   */
  private void setTexture(float[] color) {
    Identifier texture = this.textureOverride;

    if (texture == null) {
      Theme theme = ShulkerBoxTooltip.config.main.theme;

      if (theme == Theme.AUTO)
        theme = ShulkerBoxTooltipClient.isDarkModeEnabled() ? Theme.DARK : Theme.LIGHT;
      if (theme == Theme.DARK && (Arrays.equals(color, PreviewProvider.DEFAULT_COLOR)
          || Arrays.equals(color, DyeColor.WHITE.getColorComponents()))) {
        texture = DEFAULT_TEXTURE_DARK;
      } else {
        texture = DEFAULT_TEXTURE_LIGHT;
      }
    }
    RenderSystem.setShaderTexture(0, texture);
  }

  private void drawBackground(int x, int y, int z, MatrixStack matrices) {
    int zOffset = z + 100;
    int invSize = this.getInvSize();
    int xOffset = 7;
    int yOffset = 7;
    int rowTexYPos = 7;
    int rowSize = Math.min(this.getMaxRowSize(), invSize);
    int rowWidth = rowSize * 18;

    setTexture(this.setColor());

    // top side
    for (int size = rowSize; size > 0; size -= 9) {
      int s = Math.min(size, 9);

      DrawableHelper.drawTexture(matrices, x + xOffset, y, zOffset, 7, 0, s * 18, 7, 256, 256);
      xOffset += s * 18;
    }

    while (invSize > 0) {
      xOffset = 7;
      // left side
      DrawableHelper.drawTexture(matrices, x, y + yOffset, zOffset, 0, rowTexYPos, 7, 18, 256, 256);
      for (int rSize = rowSize; rSize > 0; rSize -= 9) {
        int s = Math.min(rSize, 9);

        // center
        DrawableHelper.drawTexture(matrices, x + xOffset, y + yOffset, zOffset, 7, rowTexYPos,
            s * 18, 18, 256, 256);
        xOffset += s * 18;
      }
      // right side
      DrawableHelper.drawTexture(matrices, x + xOffset, y + yOffset, zOffset, 169, rowTexYPos, 7,
          18, 256, 256);
      yOffset += 18;
      invSize -= rowSize;
      rowTexYPos = rowTexYPos >= 43 ? 7 : rowTexYPos + 18;
    }

    xOffset = 7;
    for (int size = rowSize; size > 0; size -= 9) {
      int s = Math.min(size, 9);

      // bottom side
      DrawableHelper.drawTexture(matrices, x + xOffset, y + yOffset, zOffset, 7, 61, s * 18, 7, 256,
          256);
      xOffset += s * 18;
    }

    // top-left corner
    DrawableHelper.drawTexture(matrices, x, y, zOffset, 0, 0, 7, 7, 256, 256);
    // top-right corner
    DrawableHelper.drawTexture(matrices, x + rowWidth + 7, y, zOffset, 169, 0, 7, 7, 256, 256);
    // bottom-right corner
    DrawableHelper.drawTexture(matrices, x + rowWidth + 7, y + yOffset, zOffset, 169, 61, 7, 7, 256,
        256);
    // bottom-left corner
    DrawableHelper.drawTexture(matrices, x, y + yOffset, zOffset, 0, 61, 7, 7, 256, 256);
  }

  @Override
  public void draw(int x, int y, int z, MatrixStack matrices, TextRenderer textRenderer,
      ItemRenderer itemRenderer, TextureManager textureManager) {
    if (this.items.isEmpty() || this.previewType == PreviewType.NO_PREVIEW)
      return;

    drawBackground(x, y, z, matrices);

    int maxRowSize = this.getMaxRowSize();

    itemRenderer.zOffset = z;
    if (this.previewType == PreviewType.COMPACT) {
      for (int i = 0, s = this.items.size(); i < s; ++i) {
        MergedItemStack compactor = this.items.get(i);
        int xOffset = 8 + x + 18 * (i % maxRowSize);
        int yOffset = 8 + y + 18 * (i / maxRowSize);
        ItemStack stack = compactor.get();

        itemRenderer.renderInGuiWithOverrides(stack, xOffset, yOffset);
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, xOffset, yOffset);
      }
    } else {
      for (MergedItemStack compactor : this.items) {
        for (int i = 0, size = compactor.size(); i < size; ++i) {
          int xOffset = 8 + x + 18 * (i % maxRowSize);
          int yOffset = 8 + y + 18 * (i / maxRowSize);
          ItemStack subStack = compactor.getSubStack(i);

          itemRenderer.renderInGuiWithOverrides(subStack, xOffset, yOffset);
          itemRenderer.renderGuiItemOverlay(textRenderer, subStack, xOffset, yOffset);
        }
      }
    }
    itemRenderer.zOffset = 0.0f;
  }
}
