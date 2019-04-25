package com.misterpemodder.shulkerboxtooltip;

import java.util.List;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.Configuration.ShulkerBoxTooltipType;
import com.misterpemodder.shulkerboxtooltip.hook.ShulkerPreviewPosGetter;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public final class ShulkerBoxTooltip implements ClientModInitializer {
  private static ShulkerBoxPreviewRenderer previewRenderer;
  private static Configuration config;

  @Override
  public void onInitializeClient() {
    AutoConfig.register(Configuration.class, GsonConfigSerializer::new);
    config = AutoConfig.getConfigHolder(Configuration.class).getConfig();
    LegacyConfiguration.updateConfig();
  }

  /**
   * Modifies the shulker box tooltip.
   * 
   * @param stack    The shulker box item stack
   * @param tooltip  The list to put the tooltip in.
   * @param compound The stack NBT data.
   * @return true to cancel vanilla tooltip code, false otherwise.
   */
  public static boolean buildShulkerBoxTooltip(ItemStack stack, List<TextComponent> tooltip,
      @Nullable CompoundTag compound) {
    ShulkerBoxTooltipType type = config.main.tooltipType;
    if (type == ShulkerBoxTooltipType.NONE)
      return true;
    if (type == ShulkerBoxTooltipType.VANILLA)
      return false;
    if (compound == null) {
      tooltip.add(
          new TranslatableTextComponent("container.shulkerbox.empty").applyFormat(TextFormat.GRAY));
    } else if (compound.containsKey("LootTable", NbtType.STRING)) {
      tooltip.add(new StringTextComponent(TextFormat.GRAY + "???????"));
    } else if (compound.containsKey("Items", NbtType.LIST)) {
      ListTag list = compound.getList("Items", NbtType.COMPOUND);
      if (list.size() > 0) {
        tooltip.add(new TranslatableTextComponent("container.shulkerbox.contains", list.size())
            .applyFormat(TextFormat.GRAY));
        TextComponent hint = getTooltipHint();
        if (hint != null)
          tooltip.add(hint);
      } else {
        tooltip.add(new TranslatableTextComponent("container.shulkerbox.empty")
            .applyFormat(TextFormat.GRAY));
      }
    }
    return true;
  }

  private static boolean shouldDisplayPreview() {
    return config.main.alwaysOn || Screen.hasShiftDown();
  }

  @Nullable
  private static TextComponent getTooltipHint() {
    if (!config.main.enablePreview || (shouldDisplayPreview() && Screen.hasAltDown()))
      return null;
    String keyHint =
        shouldDisplayPreview() ? (config.main.alwaysOn ? "Alt" : "Alt+Shift") : "Shift";
    String contentHint;
    if (getCurrentPreviewType() == ShulkerBoxPreviewType.NO_PREVIEW)
      contentHint = config.main.swapModes ? "viewFullContents" : "viewContents";
    else
      contentHint = config.main.swapModes ? "viewContents" : "viewFullContents";
    return new StringTextComponent(keyHint + ": ").applyFormat(TextFormat.GOLD)
        .append(new TranslatableTextComponent("container.shulkerbox." + contentHint)
            .applyFormat(TextFormat.WHITE));
  }

  /**
   * @return The shulker box tooltip type depending of which keys are pressed.
   */
  public static ShulkerBoxPreviewType getCurrentPreviewType() {
    if (config.main.swapModes) {
      if (shouldDisplayPreview())
        return Screen.hasAltDown() ? ShulkerBoxPreviewType.COMPACT : ShulkerBoxPreviewType.FULL;
    } else {
      if (shouldDisplayPreview())
        return Screen.hasAltDown() ? ShulkerBoxPreviewType.FULL : ShulkerBoxPreviewType.COMPACT;
    }
    return ShulkerBoxPreviewType.NO_PREVIEW;
  }

  /**
   * Should the shulker box previex be drawn? Also checks if the passed {@link ItemStack} is a
   * shulker box.
   * 
   * @param stack The stack to check.
   * @return true if the preview should be drawn.
   */
  public static boolean hasShulkerBoxPreview(ItemStack stack) {
    return config.main.enablePreview && getCurrentPreviewType() != ShulkerBoxPreviewType.NO_PREVIEW
        && Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock;
  }

  public static void drawShulkerBoxPreview(Screen screen, ItemStack stack, int mouseX, int mouseY) {
    if (previewRenderer == null)
      previewRenderer = new ShulkerBoxPreviewRenderer();
    previewRenderer.setShulkerStack(stack);
    previewRenderer.setPreviewType(getCurrentPreviewType());
    int x = Math.min(((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getStartX() - 1,
        screen.width - previewRenderer.getWidth());
    int y = ((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getBottomY() + 1;
    int h = previewRenderer.getHeight();
    if (config.main.lockPreview || y + h > screen.height)
      y = ((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getTopY() - h;
    previewRenderer.draw(x, y);
  }
}
