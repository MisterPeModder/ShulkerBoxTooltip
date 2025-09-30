package com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry;

import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ConfigCategoryTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class CategoryTitleConfigEntry extends ConfigEntry {
  private final Minecraft minecraft;
  private final Component label;

  public CategoryTitleConfigEntry(ConfigCategoryTab<?> tab, Component label) {
    super();
    this.minecraft = tab.getMinecraft();
    this.label = label;
  }

  @NotNull
  @Override
  public List<? extends NarratableEntry> narratables() {
    return List.of(new NarratableEntry() {
      @NotNull
      @Override
      public NarratableEntry.NarrationPriority narrationPriority() {
        return NarratableEntry.NarrationPriority.HOVERED;
      }

      @Override
      public void updateNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, CategoryTitleConfigEntry.this.label);
      }
    });
  }

  @Override
  public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float delta) {
    guiGraphics.drawCenteredString(this.minecraft.font, this.label, this.getContentXMiddle(), this.getContentY() + 5,
        -1);
  }

  @NotNull
  @Override
  public List<? extends GuiEventListener> children() {
    return List.of();
  }
}
