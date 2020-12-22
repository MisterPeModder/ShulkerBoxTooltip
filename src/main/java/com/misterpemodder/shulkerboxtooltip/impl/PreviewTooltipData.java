package com.misterpemodder.shulkerboxtooltip.impl;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;

import net.minecraft.client.item.TooltipData;

public class PreviewTooltipData implements TooltipData {
    public final PreviewProvider provider;
    public final PreviewContext context;

    public PreviewTooltipData(PreviewProvider provider, PreviewContext context) {
        this.provider = provider;
        this.context = context;
    }
}
