package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.inventory.Inventory;

import java.util.function.Supplier;

/**
 * A {@link BlockEntityPreviewProvider} that uses a {@link BlockEntity} instance to get its information.
 */
public class BlockEntityAwarePreviewRenderer<BE extends BlockEntity & Inventory> extends BlockEntityPreviewProvider {

  private final Supplier<? extends BE> blockEntityFactory;

  private final ThreadLocal<BE> cachedBlockEntity = ThreadLocal.withInitial(() -> null);

  public BlockEntityAwarePreviewRenderer(int maxRowSize, Supplier<? extends BE> blockEntityFactory) {
    super(27, false, maxRowSize);
    this.blockEntityFactory = blockEntityFactory;
  }

  private BE getBlockEntity() {
    BE be = this.cachedBlockEntity.get();
    if (be == null) {
      be = this.blockEntityFactory.get();
      this.cachedBlockEntity.set(be);
    }
    return be;
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return this.getBlockEntity().size();
  }

  @Override
  public boolean canUseLootTables() {
    return this.getBlockEntity() instanceof LootableContainerBlockEntity;
  }
}
