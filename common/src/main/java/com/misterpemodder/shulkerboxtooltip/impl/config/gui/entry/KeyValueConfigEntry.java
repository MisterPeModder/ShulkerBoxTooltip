package com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry;

import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ConfigCategoryTab;
import com.misterpemodder.shulkerboxtooltip.impl.tree.ValueConfigNode;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public final class KeyValueConfigEntry<C> extends ValueConfigEntry<C, Key, Key> {
  private final Button keyButton;

  public KeyValueConfigEntry(ConfigCategoryTab<C> tab, ValueConfigNode<C, Key, Key> valueNode) {
    super(tab, valueNode);

    var label = this.valueNode.getTitle();
    this.keyButton = Button.builder(label, b -> {
      this.tab.setSelectedKeyNode(this.valueNode);
      this.tab.getScreen().refresh();
    }).bounds(0, 0, 160, 20).createNarration(supplier -> this.getValue().isUnbound() ?
        Component.translatable("narrator.controls.unbound", label) :
        Component.translatable("narrator.controls.bound", label, supplier.get())).build();
    this.children.addFirst(this.keyButton);
    this.refresh();
  }

  @Override
  public void resetToDefault() {
    super.resetToDefault();
    this.tab.setSelectedKeyNode(null);
    this.tab.getScreen().refresh();
  }

  @Override
  public void refresh() {
    super.refresh();
    this.keyButton.setMessage(this.getValue().get().getDisplayName());

    if (this.tab.getSelectedKeyNode() == this.valueNode) {
      this.keyButton.setMessage(Component.literal("> ")
          .append(this.keyButton.getMessage().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
          .append(" <")
          .withStyle(ChatFormatting.YELLOW));
    }
  }

  @Override
  public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovered, float delta) {
    int x = this.getContentX();
    int y = this.getContentY();
    this.renderLabel(guiGraphics);

    this.keyButton.setWidth(160 - this.resetButton.getWidth() - 2 - this.undoButton.getWidth() - 2);
    if (this.tab.getMinecraft().font.isBidirectional()) {
      this.undoButton.setX(x);
      this.undoButton.setY(y);

      this.resetButton.setX(x + undoButton.getWidth() + 2);
      this.resetButton.setY(y);

      this.keyButton.setX(x + undoButton.getWidth() + 2 + resetButton.getWidth() + 2);
      this.keyButton.setY(y);
    } else {
      this.undoButton.setX(this.getContentRight() - this.undoButton.getWidth());
      this.undoButton.setY(y);

      this.resetButton.setX(this.undoButton.getX() - this.resetButton.getWidth() - 2);
      this.resetButton.setY(y);

      this.keyButton.setX(this.resetButton.getX() - this.keyButton.getWidth() - 2);
      this.keyButton.setY(y);
    }

    this.keyButton.extractRenderState(guiGraphics, mouseX, mouseY, delta);
    this.resetButton.extractRenderState(guiGraphics, mouseX, mouseY, delta);
    this.undoButton.extractRenderState(guiGraphics, mouseX, mouseY, delta);
  }
}
