package com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry;

import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ConfigCategoryTab;
import com.misterpemodder.shulkerboxtooltip.impl.tree.ValueConfigNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class ValueConfigEntry<C, T, V> extends ConfigEntry {
  private final Component label;
  private final Component labelChanged;
  private final Component labelError;
  private final Component labelErrorChanged;

  @Nullable
  private final List<FormattedCharSequence> tooltip;
  private List<FormattedCharSequence> tooltipWithError;
  protected final ConfigCategoryTab<C> tab;
  protected final List<AbstractWidget> children = Lists.newArrayList();
  protected final ValueConfigNode<C, T, V> valueNode;
  public final Button resetButton;
  public final Button undoButton;
  @Nullable
  private Component validationError;
  private boolean hasChanged;

  public static final Component RESET_BUTTON_LABEL = Component.translatable(
      "shulkerboxtooltip.config.reset_to_default.small");
  public static final Component RESET_BUTTON_TOOLTIP = Component.translatable(
      "shulkerboxtooltip.config.reset_to_default.full");
  public static final Component UNDO_BUTTON_LABEL = Component.translatable("shulkerboxtooltip.config.undo.small");
  public static final Component UNDO_BUTTON_TOOLTIP = Component.translatable("shulkerboxtooltip.config.undo.full");

  protected ValueConfigEntry(ConfigCategoryTab<C> tab, ValueConfigNode<C, T, V> valueNode) {
    this.tab = tab;
    this.label = valueNode.getTitle().copy().withStyle(ChatFormatting.GRAY);
    this.labelChanged = label.copy().withStyle(ChatFormatting.ITALIC, ChatFormatting.WHITE);
    this.labelError = label.copy().withStyle(ChatFormatting.RED);
    this.labelErrorChanged = label.copy().withStyle(ChatFormatting.ITALIC, ChatFormatting.RED);

    this.tooltip = valueNode.getTooltip() == null ? null : tab.getMinecraft().font.split(valueNode.getTooltip(), 350);
    this.tooltipWithError = this.getTooltipWithError();
    this.valueNode = valueNode;
    this.resetButton = Button.builder(RESET_BUTTON_LABEL, b -> this.resetToDefault()).bounds(0, 0,
        Math.max(tab.getMinecraft().font.width(RESET_BUTTON_LABEL) + 6, 20), 20).build();
    this.resetButton.active = !valueNode.isDefaultValue(this.tab.getConfig());
    this.children.add(this.resetButton);
    this.undoButton = Button.builder(UNDO_BUTTON_LABEL, b -> this.resetToActive()).bounds(0, 0,
        Math.max(tab.getMinecraft().font.width(UNDO_BUTTON_LABEL) + 6, 20), 20).build();
    this.undoButton.active = !valueNode.isActiveValue(this.tab.getConfig());
    this.children.add(this.undoButton);
  }

  public void resetToDefault() {
    this.valueNode.resetToDefault();
    this.tab.getScreen().refresh();
  }

  public void resetToActive() {
    this.valueNode.resetToActive(this.tab.getConfig());
    this.tab.getScreen().refresh();
  }

  public V getValue() {
    return this.valueNode.getEditingValue(this.tab.getConfig());
  }

  public void setValue(V value) {
    this.valueNode.setEditingValue(value);
    this.tab.getScreen().refresh();
  }

  public void refresh() {
    this.resetButton.active = !valueNode.isDefaultValue(this.tab.getConfig());
    this.undoButton.active = !valueNode.isActiveValue(this.tab.getConfig());
    this.validationError = this.valueNode.validate(this.tab.getConfig());
    this.hasChanged = !valueNode.isActiveValue(this.tab.getConfig());
    this.tooltipWithError = this.getTooltipWithError();
  }

  @NotNull
  @Override
  public List<? extends GuiEventListener> children() {
    return this.children;
  }

  @NotNull
  @Override
  public List<? extends NarratableEntry> narratables() {
    return this.children;
  }

  @Nullable
  public List<FormattedCharSequence> getTooltip() {
    return this.validationError != null ? this.tooltipWithError : this.tooltip;
  }

  private List<FormattedCharSequence> getTooltipWithError() {
    if (this.validationError != null) {
      List<FormattedCharSequence> errorTooltip = new ArrayList<>();

      if (this.tooltip != null)
        errorTooltip.addAll(this.tooltip);
      errorTooltip.add(this.validationError.copy().withStyle(ChatFormatting.RED).getVisualOrderText());
      return errorTooltip;
    }
    return this.tooltip;
  }

  protected void renderLabel(GuiGraphicsExtractor guiGraphics) {
    int x = this.getContentX();
    int y = this.getContentY();
    Component l;

    if (this.validationError != null) {
      l = this.hasChanged ? this.labelErrorChanged : this.labelError;
    } else {
      l = this.hasChanged ? this.labelChanged : this.label;
    }

    if (this.tab.getMinecraft().font.isBidirectional()) {
      x = x + this.getContentWidth() - this.tab.getMinecraft().font.width(l);
    }
    guiGraphics.text(this.tab.getMinecraft().font, l.getVisualOrderText(), x, y + 5, -1, false);
  }
}
