package com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry;

import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ConfigCategoryTab;
import com.misterpemodder.shulkerboxtooltip.impl.tree.ValueConfigNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public final class IntegerValueConfigEntry<C> extends ValueConfigEntry<C, Integer, Integer> {
  private final EditBox inputField;
  private boolean isValid;

  private static final Pattern INTEGER_PATTERN = Pattern.compile("-?\\d*");

  public IntegerValueConfigEntry(ConfigCategoryTab<C> tab, ValueConfigNode<C, Integer, Integer> valueNode) {
    super(tab, valueNode);

    this.isValid = true;
    this.inputField = new EditBox(tab.getMinecraft().font, 0, 0, 158, 18, this.valueNode.getTitle());
    this.inputField.setValue(this.getValue().toString());
    this.inputField.setResponder(this::onInputChange);
    this.inputField.addFormatter(this::formatField);
    this.children.addFirst(this.inputField);
  }

  @Override
  public void refresh() {
    this.isValid = this.valueNode.validate(this.tab.getConfig()) == null;
    if (this.isValid) {
      var valueStr = this.getValue().toString();
      if (!this.inputField.getValue().equals(valueStr)) {
        this.inputField.setValue(valueStr);
      }
    }
    super.refresh();
  }

  private void onInputChange(String value) {
    try {
      this.setValue(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      // Ignore
    }
  }

  private FormattedCharSequence formatField(String s, int i) {
    if (this.isValid) {
      return FormattedCharSequence.forward(s, Style.EMPTY);
    } else {
      return FormattedCharSequence.forward(s, Style.EMPTY.withColor(ChatFormatting.RED));
    }
  }

  @Override
  public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovered, float delta) {
    int x = this.getContentX();
    int y = this.getContentY();
    int right = this.getContentRight();

    this.renderLabel(guiGraphics);
    this.inputField.setX(right - 158 - 1);
    this.inputField.setY(y + 1);

    this.resetButton.setX(right - this.resetButton.getWidth() - 2 - this.undoButton.getWidth());
    this.resetButton.setY(y);

    this.undoButton.setX(right - this.undoButton.getWidth());
    this.undoButton.setY(y);

    this.inputField.setWidth(158 - this.resetButton.getWidth() - 2 - this.undoButton.getWidth() - 2);
    if (this.tab.getMinecraft().font.isBidirectional()) {
      this.undoButton.setX(x);
      this.undoButton.setY(y);

      this.resetButton.setX(this.undoButton.getX() + this.undoButton.getWidth() + 2);
      this.resetButton.setY(y);

      this.inputField.setX(this.resetButton.getX() + this.resetButton.getWidth() + 2);
      this.inputField.setY(y + 1);
    } else {
      this.undoButton.setX(right - this.undoButton.getWidth());
      this.undoButton.setY(y);

      this.resetButton.setX(this.undoButton.getX() - this.resetButton.getWidth() - 2);
      this.resetButton.setY(y);

      this.inputField.setX(this.resetButton.getX() - this.inputField.getWidth() - 3);
      this.inputField.setY(y + 1);
    }

    this.inputField.extractRenderState(guiGraphics, mouseX, mouseY, delta);
    this.resetButton.extractRenderState(guiGraphics, mouseX, mouseY, delta);
    this.undoButton.extractRenderState(guiGraphics, mouseX, mouseY, delta);
  }
}
