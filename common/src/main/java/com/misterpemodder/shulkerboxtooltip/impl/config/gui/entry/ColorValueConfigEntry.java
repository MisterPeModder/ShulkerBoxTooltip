package com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry;

import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ColorWidget;
import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ConfigCategoryTab;
import com.misterpemodder.shulkerboxtooltip.impl.tree.ValueConfigNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

@Environment(EnvType.CLIENT)
public final class ColorValueConfigEntry<C> extends ValueConfigEntry<C, ColorKey, Integer> {
  private final ColorWidget colorWidget;
  private final EditBox inputField;
  private boolean isValid;

  public ColorValueConfigEntry(ConfigCategoryTab<C> tab, ValueConfigNode<C, ColorKey, Integer> valueNode) {
    super(tab, valueNode);

    this.isValid = true;
    this.inputField = new EditBox(tab.getMinecraft().font, 0, 0, 138, 18, this.valueNode.getTitle());
    this.inputField.setValue(this.displayValue());
    this.inputField.setResponder(this::onInputChange);
    this.colorWidget = new ColorWidget(this.valueNode.getTitle(), this.inputField, this::getValue);
    this.children.addFirst(this.colorWidget);
    this.children.addFirst(this.inputField);
    this.inputField.addFormatter(this::formatField);
  }

  @Override
  public void refresh() {
    this.isValid = this.valueNode.validate(this.tab.getConfig()) == null;
    if (this.isValid) {
      var valueStr = this.displayValue();
      if (!this.inputField.getValue().equals(valueStr)) {
        this.inputField.setValue(valueStr);
      }
    }
    super.refresh();
  }

  private void onInputChange(String value) {
    int argb;

    try {
      if (value.startsWith("#")) {
        if (value.length() > 7) {
          this.setValue(0xFFFFFFFF);
          return;
        }
        argb = (int) Long.parseLong(value.substring(1), 16);
      } else {
        argb = (int) Long.parseLong(value, 16);
      }
    } catch (NumberFormatException e) {
      this.setValue(0xFFFFFFFF);
      return;
    }
    this.setValue(argb);
  }

  private FormattedCharSequence formatField(String s, int i) {
    if (this.isValid) {
      return FormattedCharSequence.forward(s, Style.EMPTY);
    } else {
      return FormattedCharSequence.forward(s, Style.EMPTY.withColor(ChatFormatting.RED));
    }
  }

  private String displayValue() {
    return "#" + Integer.toHexString(this.getValue());
  }

  @Override
  public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float delta) {
    int x = this.getContentX();
    int y = this.getContentY();
    this.renderLabel(guiGraphics);

    this.inputField.setWidth(138 - this.resetButton.getWidth() - 2 - this.undoButton.getWidth() - 2);
    if (this.tab.getMinecraft().font.isBidirectional()) {
      this.undoButton.setX(x);
      this.undoButton.setY(y);

      this.resetButton.setX(this.undoButton.getX() + this.undoButton.getWidth() + 2);
      this.resetButton.setY(y);

      this.inputField.setX(this.resetButton.getX() + this.resetButton.getWidth() + 2);
      this.inputField.setY(y + 1);

      this.colorWidget.setX(this.inputField.getX() + this.inputField.getWidth() + 2);
      this.colorWidget.setY(y + 1);
    } else {
      this.undoButton.setX(this.getContentRight() - this.undoButton.getWidth());
      this.undoButton.setY(y);

      this.resetButton.setX(this.undoButton.getX() - this.resetButton.getWidth() - 2);
      this.resetButton.setY(y);

      this.inputField.setX(this.resetButton.getX() - this.inputField.getWidth() - 3);
      this.inputField.setY(y + 1);

      this.colorWidget.setX(this.inputField.getX() - this.colorWidget.getWidth() - 2);
      this.colorWidget.setY(y + 1);
    }

    this.colorWidget.render(guiGraphics, mouseX, mouseY, delta);
    this.inputField.render(guiGraphics, mouseX, mouseY, delta);
    this.resetButton.render(guiGraphics, mouseX, mouseY, delta);
    this.undoButton.render(guiGraphics, mouseX, mouseY, delta);
  }
}
