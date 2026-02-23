package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A generic preview provider for any item that has the {@code minecraft:container} data component.
 */
public class GenericContainerPreviewProvider extends BlockEntityPreviewProvider {
  public static final GenericContainerPreviewProvider INSTANCE = new GenericContainerPreviewProvider();

  private GenericContainerPreviewProvider() {
    super(27, true, 9);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public ColorKey getWindowColorKey(PreviewContext context) {
    return ColorKey.GENERIC_CONTAINER;
  }

  @Override
  public int getPriority() {
    return 0;
  }
}
