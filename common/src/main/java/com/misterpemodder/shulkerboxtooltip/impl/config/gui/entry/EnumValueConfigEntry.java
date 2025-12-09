package com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry;

import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ConfigCategoryTab;
import com.misterpemodder.shulkerboxtooltip.impl.tree.ValueConfigNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class EnumValueConfigEntry<C, E extends Enum<E>> extends ValueConfigEntry<C, E, E> {
  private final CycleButton<E> valueButton;

  public EnumValueConfigEntry(ConfigCategoryTab<C> tab, ValueConfigNode<C, E, E> valueNode) {
    super(tab, valueNode);

    this.valueButton = CycleButton.<E>builder(value -> Component.translatable(value.toString()), this.getValue())
        .displayOnlyValue()
        .withValues(List.of(this.valueNode.getValueType().getEnumConstants()))
        .create(0, 0, 160, 20, this.valueNode.getTitle(), (b, value) -> this.setValue(value));
    this.children.addFirst(this.valueButton);
  }

  @Override
  public void refresh() {
    super.refresh();
    var value = this.getValue();
    if (this.valueButton.getValue() != value) {
      this.valueButton.setValue(value);
    }
  }

  @Override
  public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float delta) {
    int x = this.getContentX();
    int y = this.getContentY();
    this.renderLabel(guiGraphics);

    this.valueButton.setWidth(160 - this.resetButton.getWidth() - 2 - this.undoButton.getWidth() - 2);
    if (this.tab.getMinecraft().font.isBidirectional()) {
      this.undoButton.setX(x);
      this.undoButton.setY(y);

      this.resetButton.setX(x + undoButton.getWidth() + 2);
      this.resetButton.setY(y);

      this.valueButton.setX(x + undoButton.getWidth() + 2 + resetButton.getWidth() + 2);
      this.valueButton.setY(y);
    } else {
      this.undoButton.setX(this.getContentRight() - this.undoButton.getWidth());
      this.undoButton.setY(y);

      this.resetButton.setX(this.undoButton.getX() - this.resetButton.getWidth() - 2);
      this.resetButton.setY(y);

      this.valueButton.setX(this.resetButton.getX() - this.valueButton.getWidth() - 2);
      this.valueButton.setY(y);
    }

    this.valueButton.render(guiGraphics, mouseX, mouseY, delta);
    this.resetButton.render(guiGraphics, mouseX, mouseY, delta);
    this.undoButton.render(guiGraphics, mouseX, mouseY, delta);
  }
}
