package com.misterpemodder.shulkerboxtooltip.impl;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;

public class PreviewTooltipComponent implements TooltipComponent {
    private final PreviewRenderer renderer;

    public PreviewTooltipComponent(PreviewTooltipData data) {
        PreviewProvider provider = data.provider;
        PreviewContext context = data.context;
        PreviewRenderer renderer = data.provider.getRenderer();

        if (renderer == null)
            renderer = PreviewRenderer.getDefaultRendererInstance();
        renderer.setPreview(context, provider);
        renderer.setPreviewType(ShulkerBoxTooltipApi.getCurrentPreviewType(provider.isFullPreviewAvailable(context)));
        this.renderer = renderer;
    }

    @Override
    public int getHeight() {
        return this.renderer.getHeight();
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.renderer.getWidth();
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer,
            int z, TextureManager textureManager) {
        this.renderer.draw(x, y, z);
    }
}
