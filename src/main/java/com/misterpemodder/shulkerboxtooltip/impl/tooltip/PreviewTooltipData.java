package com.misterpemodder.shulkerboxtooltip.impl.tooltip;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;

import net.minecraft.client.item.TooltipData;

public record PreviewTooltipData(PreviewProvider provider,
                                 PreviewContext context) implements TooltipData {
}
