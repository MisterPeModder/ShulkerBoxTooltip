package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public record FixedPreviewProviderRegistry<I extends Container>(PreviewProviderRegistry registry,
                                                                BiFunction<Integer, Supplier<I>, PreviewProvider> providerFactory) {
  public FixedPreviewProviderRegistry<I> register(String id, int maxRowSize,
      BiFunction<BlockPos, BlockState, I> inventoryFactory, Block block) {
    var provider = providerFactory.apply(maxRowSize,
        () -> inventoryFactory.apply(BlockPos.ZERO, block.defaultBlockState()));
    registry.register(ShulkerBoxTooltipUtil.id(id), provider, block.asItem());
    return this;
  }

  public FixedPreviewProviderRegistry<I> registerCollection(String id, int maxRowSize,
      BiFunction<BlockPos, BlockState, I> inventoryFactory, List<String> prefixes, List<Block> blocks) {
    ShulkerBoxTooltipUtil.zipApply(prefixes, blocks,
        (prefix, block) -> this.register(prefix + id, maxRowSize, inventoryFactory, block));
    return this;
  }
}
