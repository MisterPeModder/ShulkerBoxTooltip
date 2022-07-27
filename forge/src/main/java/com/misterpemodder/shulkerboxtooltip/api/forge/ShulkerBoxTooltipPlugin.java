package com.misterpemodder.shulkerboxtooltip.api.forge;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import net.minecraftforge.fml.IExtensionPoint;

import java.util.function.Supplier;

/**
 * <b>Forge-specific API, do not use on Fabric/Quilt!</b>
 *
 * @param apiImplSupplier A function that returns an instance of {@link ShulkerBoxTooltipApi}.
 * @since 3.1.0
 */
public record ShulkerBoxTooltipPlugin(Supplier<ShulkerBoxTooltipApi> apiImplSupplier)
    implements IExtensionPoint<ShulkerBoxTooltipPlugin> {
}
