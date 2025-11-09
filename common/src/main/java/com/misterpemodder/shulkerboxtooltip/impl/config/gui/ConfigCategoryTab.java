package com.misterpemodder.shulkerboxtooltip.impl.config.gui;

import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry.*;
import com.misterpemodder.shulkerboxtooltip.impl.tree.CategoryConfigNode;
import com.misterpemodder.shulkerboxtooltip.impl.tree.ValueConfigNode;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ConfigCategoryTab<C> implements Tab {
  private final ConfigScreen<C> screen;
  private final CategoryConfigNode<C> category;
  private final C config;

  private final Component title;
  private final Component titleChanged;
  private final Component titleError;
  private final Component titleErrorChanged;

  private final ConfigEntryList list;

  @Nullable
  private ValueConfigNode<C, Key, Key> selectedKeyNode;

  @Nullable
  private TabButton tabButton;

  public ConfigCategoryTab(ConfigScreen<C> screen, CategoryConfigNode<C> category, C config) {
    this.screen = screen;
    this.category = category;
    this.config = config;

    this.title = category.getTitle();
    this.titleChanged = title.copy().withStyle(ChatFormatting.ITALIC);
    this.titleError = title.copy().withStyle(ChatFormatting.RED);
    this.titleErrorChanged = title.copy().withStyle(ChatFormatting.ITALIC, ChatFormatting.RED);

    List<ConfigEntry> entries = new ArrayList<>();

    for (var node : category.getChildren()) {
      if (node.getPrefix() != null) {
        entries.add(new PrefixTextConfigEntry(this, node.getPrefix()));
      }
      if (node instanceof ValueConfigNode<C, ?, ?> valueNode) {
        entries.add(this.createValueEntry(valueNode));
      } else if (node instanceof CategoryConfigNode<C> categoryNode) {
        entries.addAll(this.createSubCategoryEntries(categoryNode));
      }
    }

    this.list = new ConfigEntryList(this, this.getMinecraft(), this.screen.width,
        this.screen.height - this.screen.getHeaderHeight() - this.screen.getFooterHeight(),
        this.screen.getHeaderHeight(), 24, entries);
  }

  @NotNull
  @Override
  public Component getTabTitle() {
    return this.title;
  }

  @NotNull
  @Override
  public Component getTabExtraNarration() {
    return Component.empty();
  }

  @Override
  public void visitChildren(Consumer<AbstractWidget> consumer) {
    consumer.accept(this.list);
  }

  @Override
  public void doLayout(ScreenRectangle screenRectangle) {
    this.list.setRectangle(screenRectangle.width(), screenRectangle.height(), screenRectangle.left(),
        screenRectangle.top());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private <T, V> ConfigEntry createValueEntry(ValueConfigNode<C, T, V> valueNode) {
    Class<? extends T> type = valueNode.getType();

    if (type.equals(Boolean.class)) {
      return new BooleanValueConfigEntry<>(this, (ValueConfigNode<C, Boolean, Boolean>) valueNode);
    } else if (ColorKey.class.isAssignableFrom(type)) {
      return new ColorValueConfigEntry<>(this, (ValueConfigNode<C, ColorKey, Integer>) valueNode);
    } else if (Enum.class.isAssignableFrom(type)) {
      return new EnumValueConfigEntry<>(this, (ValueConfigNode<C, Enum, Enum>) valueNode);
    } else if (type.equals(Integer.class)) {
      return new IntegerValueConfigEntry<>(this, (ValueConfigNode<C, Integer, Integer>) valueNode);
    } else if (Key.class.isAssignableFrom(type)) {
      return new KeyValueConfigEntry<>(this, (ValueConfigNode<C, Key, Key>) valueNode);
    } else {
      throw new UnsupportedOperationException("Unsupported type: " + type);
    }
  }

  private List<ConfigEntry> createSubCategoryEntries(CategoryConfigNode<C> categoryNode) {
    var entries = new ArrayList<ConfigEntry>(categoryNode.getChildren().size() + 1);
    entries.add(new CategoryTitleConfigEntry(this, categoryNode.getTitle()));
    for (var node : categoryNode.getChildren()) {
      if (node instanceof ValueConfigNode<C, ?, ?> valueNode) {
        entries.add(this.createValueEntry(valueNode));
      }
    }
    return entries;
  }

  @NotNull
  public Minecraft getMinecraft() {
    return Objects.requireNonNull(this.screen.getMinecraft());
  }

  public ConfigScreen<C> getScreen() {
    return this.screen;
  }

  @Nullable
  public ValueConfigNode<C, Key, Key> getSelectedKeyNode() {
    return this.selectedKeyNode;
  }

  public void setSelectedKeyNode(@Nullable ValueConfigNode<C, Key, Key> selectedKeyNode) {
    this.selectedKeyNode = selectedKeyNode;
  }

  public void refresh() {
    if (this.tabButton == null) {
      return;
    }
    Component newTitle;
    var hasChanged = !this.category.isActiveValue(this.config);
    var hasError = this.category.validate(this.config) != null;

    if (hasError) {
      newTitle = hasChanged ? this.titleErrorChanged : this.titleError;
    } else {
      newTitle = hasChanged ? this.titleChanged : this.title;
    }
    tabButton.setMessage(newTitle);
    this.list.refreshEntries();
  }

  public C getConfig() {
    return this.config;
  }

  public boolean keyPressed(KeyEvent event) {
    if (this.selectedKeyNode != null) {
      if (event.isEscape()) {
        this.selectedKeyNode.setEditingValue(Key.UNKNOWN_KEY);
      } else {
        this.selectedKeyNode.setEditingValue(new Key(InputConstants.getKey(event)));
      }

      this.selectedKeyNode = null;
      this.screen.refresh();
      return true;
    }
    return false;
  }

  public void setTabButton(@Nullable TabButton tabButton) {
    this.tabButton = tabButton;
  }
}
