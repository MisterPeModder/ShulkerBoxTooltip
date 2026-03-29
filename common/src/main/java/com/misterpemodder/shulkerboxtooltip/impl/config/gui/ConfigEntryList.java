package com.misterpemodder.shulkerboxtooltip.impl.config.gui;

import com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry.ConfigEntry;
import com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry.ValueConfigEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

@Environment(EnvType.CLIENT)
public final class ConfigEntryList extends ContainerObjectSelectionList<ConfigEntry> {
  private final ConfigCategoryTab<?> tab;

  public ConfigEntryList(ConfigCategoryTab<?> tab, Minecraft minecraft, int width, int contentHeight, int headerHeight,
      int itemSpacing, Iterable<ConfigEntry> entries) {
    super(minecraft, width, contentHeight, headerHeight, itemSpacing);
    this.tab = tab;
    entries.forEach(this::addEntry);
  }

  @Override
  public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
    super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, delta);
    var entry = this.getHovered();
    if (entry != null) {
      if (entry instanceof ValueConfigEntry<?, ?, ?> valueEntry) {
        if (valueEntry.resetButton.isHovered()) {
          guiGraphics.setTooltipForNextFrame(ValueConfigEntry.RESET_BUTTON_TOOLTIP, mouseX, mouseY);
          return;
        } else if (valueEntry.undoButton.isHovered()) {
          guiGraphics.setTooltipForNextFrame(ValueConfigEntry.UNDO_BUTTON_TOOLTIP, mouseX, mouseY);
          return;
        }
      }
      if (entry.getTooltip() != null) {
        guiGraphics.setTooltipForNextFrame(entry.getTooltip(), mouseX, mouseY);
      }
    }
  }

  @Override
  public int getRowWidth() {
    return this.width - 80;
  }

  public void refreshEntries() {
    this.children().forEach(ConfigEntry::refresh);
  }

  @Override
  protected void extractListSeparators(GuiGraphicsExtractor guiGraphics) {
    // don't render separators
  }
}
