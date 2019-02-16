package com.misterpemodder.shulkerboxtooltip;

import java.util.List;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.hook.ShulkerPreviewPosGetter;
import com.misterpemodder.shulkerboxtooltip.mixin.ShulkerBoxSlotsAccessor;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.InventoryUtil;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public final class ShulkerBoxTooltip {
  private static final Identifier TEXTURE =
      new Identifier("shulkerbox-tooltip", "textures/gui/shulker_box_tooltip.png");
  private static final int WIDTH = 176;
  private static final int HEIGHT = 68;

  /**
   * Modifies the shulker box tooltip.
   * 
   * @return true to cancel vanilla tooltip code, false otherwise.
   */
  public static boolean buildShulkerBoxTooltip(ItemStack stack, @Nullable BlockView view,
      List<TextComponent> tooltip, TooltipOptions options, @Nullable CompoundTag compound) {
    if (compound == null) {
      tooltip.add(new StringTextComponent(TextFormat.DARK_GRAY + "empty"));
    } else if (compound.containsKey("LootTable", NbtType.STRING)) {
      tooltip.add(new StringTextComponent("???????"));
    } else if (compound.containsKey("Items", NbtType.LIST)) {
      ListTag list = compound.getList("Items", NbtType.COMPOUND);
      if (list.size() > 0)
        tooltip.add(new StringTextComponent("Contains " + list.size() + " item(s)"));
      else
        tooltip.add(new StringTextComponent(TextFormat.DARK_GRAY + "empty"));
    }
    if (!Screen.isShiftPressed()) {
      tooltip
          .add(new StringTextComponent(TextFormat.GRAY + "" + TextFormat.ITALIC + "<press shift>"));
    }
    return true;
  }

  /**
   * Should the shulker box previex be drawn? Also checks if the passed {@link ItemStack} is a
   * shulker box.
   * 
   * @param stack The stack to check.
   * @return true if the preview should be drawn.
   */
  public static boolean hasShulkerBoxPreview(ItemStack stack) {
    return Screen.isShiftPressed()
        && Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock;
  }

  private static void drawShulkerBoxPreviewBackground(ItemStack stack, int x, int y) {
    DyeColor color = ((ShulkerBoxBlock) Block.getBlockFromItem(stack.getItem())).getColor();
    if (color != null) {
      float[] components = color.getColorComponents();
      GlStateManager.color4f(Math.max(0.15f, components[0]), Math.max(0.15f, components[1]),
          Math.max(0.15f, components[2]), 1.0f);
    } else {
      GlStateManager.color4f(0.592f, 0.403f, 0.592f, 1.0f);
    }

    MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
    GuiLighting.disable();
    double zOffset = 800.0;
    Tessellator tessellator10 = Tessellator.getInstance();
    BufferBuilder bufferBuilder11 = tessellator10.getBufferBuilder();
    bufferBuilder11.begin(7, VertexFormats.POSITION_UV);
    bufferBuilder11.vertex(x, y + HEIGHT, zOffset).texture(0, HEIGHT * 0.00390625f).next();
    bufferBuilder11.vertex(x + WIDTH, y + HEIGHT, zOffset)
        .texture(WIDTH * 0.00390625f, HEIGHT * 0.00390625f).next();
    bufferBuilder11.vertex(x + WIDTH, y + 0, zOffset).texture(WIDTH * 0.00390625f, 0).next();
    bufferBuilder11.vertex(x + 0, y + 0, zOffset).texture(0, 0).next();
    tessellator10.draw();
    GuiLighting.enable();
  }

  public static void drawShulkerBoxPreview(Screen screen, ItemStack stack, int mouseX, int mouseY) {
    int x = ((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getStartX() - 1;
    int y = ((ShulkerPreviewPosGetter) screen).shulkerboxtooltip$getStartY() + 1;
    drawShulkerBoxPreviewBackground(stack, x, y);
    CompoundTag compound = stack.getSubCompoundTag("BlockEntityTag");
    if (compound == null || !compound.containsKey("Items", NbtType.LIST))
      return;

    DefaultedList<ItemStack> items =
        DefaultedList.create(ShulkerBoxSlotsAccessor.getAvailableSlots().length, ItemStack.EMPTY);
    InventoryUtil.deserialize(compound, items);
    GuiLighting.enableForItems();
    MinecraftClient client = MinecraftClient.getInstance();
    ItemRenderer itemRenderer = client.getItemRenderer();
    FontRenderer fontRenderer = client.fontRenderer;

    itemRenderer.zOffset = 800.0f;
    for (int index = 0, size = items.size(); index < size; ++index) {
      int xOffset = 8 + x + 18 * (index % 9);
      int yOffset = 8 + y + 18 * (index / 9);
      itemRenderer.renderGuiItem(client.player, items.get(index), xOffset, yOffset);
      itemRenderer.renderGuiItemOverlay(fontRenderer, items.get(index), xOffset, yOffset);
    }
    itemRenderer.zOffset = 0.0f;
  }
}
