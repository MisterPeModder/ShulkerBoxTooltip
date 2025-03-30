package com.misterpemodder.shulkerboxtooltip.impl.tree;

import com.google.common.collect.ImmutableList;
import com.misterpemodder.shulkerboxtooltip.impl.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

public final class CategoryConfigNode<C> implements ConfigNode<C> {
  private String name;
  private Component title;
  private Component tooltip;
  private ImmutableList<ConfigNode<C>> children;

  public static final Component MULTIPLE_ERRORS = Component.translatable(
      "shulkerboxtooltip.config.validator.multiple_errors");

  private CategoryConfigNode() {
  }

  public static <C> Builder<C> builder() {
    return new Builder<>();
  }

  @NotNull
  @Override
  public String getName() {
    return this.name;
  }

  @NotNull
  @Override
  public Component getTitle() {
    return this.title;
  }

  @Nullable
  @Override
  public Component getTooltip() {
    return this.tooltip;
  }

  @Nullable
  @Override
  public Component getPrefix() {
    return null;
  }

  @Override
  public void resetToDefault() {
    this.children.forEach(ConfigNode::resetToDefault);
  }

  @Override
  public void resetToActive(C config) {
    this.children.forEach(child -> child.resetToActive(config));
  }

  @Override
  public boolean restartRequired(C config) {
    return this.children.stream().anyMatch(configNode -> configNode.restartRequired(config));
  }

  @Override
  public boolean isDefaultValue(C config) {
    return this.children.stream().allMatch(node -> node.isDefaultValue(config));
  }

  @Override
  public boolean isActiveValue(C config) {
    return this.children.stream().allMatch(node -> node.isActiveValue(config));
  }

  @Nullable
  @Override
  public Component validate(C config) {
    Component error = null;

    for (var node : this.children) {
      var result = node.validate(config);
      if (result != null) {
        if (error != null) {
          return CategoryConfigNode.MULTIPLE_ERRORS;
        }
        error = result;
      }
    }
    return error;
  }

  @NotNull
  public ImmutableList<ConfigNode<C>> getChildren() {
    return this.children;
  }

  @Override
  public void writeToNbt(C config, CompoundTag compound) {
    var subTag = new CompoundTag();
    this.children.forEach(node -> node.writeToNbt(config, subTag));
    if (!subTag.isEmpty())
      compound.put(this.getName(), subTag);
  }

  @Override
  public void readFromNbt(C config, CompoundTag compound) {
    compound.getCompound(this.getName()).ifPresent(subTag -> {
      this.children.forEach(node -> node.readFromNbt(config, subTag));
    });
  }

  @Override
  public void copy(C from, C to) {
    this.children.forEach(node -> node.copy(from, to));
  }

  @Override
  public void writeEditingToConfig(C config) {
    this.children.forEach(node -> node.writeEditingToConfig(config));
  }

  public static final class Builder<C> {
    private CategoryConfigNode<C> node;
    private ImmutableList.Builder<ConfigNode<C>> childrenBuilder;

    private Builder() {
      this.node = new CategoryConfigNode<>();
      this.childrenBuilder = ImmutableList.builder();
    }

    public Builder<C> name(String name) {
      this.node.name = name;
      return this;
    }

    public Builder<C> title(Component title) {
      this.node.title = title;
      return this;
    }

    public Builder<C> tooltip(Component tooltip) {
      this.node.tooltip = tooltip;
      return this;
    }

    public <T, V> Builder<C> value(UnaryOperator<ValueConfigNode.Builder<C, T, V>> valueBuilder) {
      this.childrenBuilder.add(valueBuilder.apply(ValueConfigNode.builder()).category(this.node).build());
      return this;
    }

    public Builder<C> category(UnaryOperator<Builder<C>> categoryBuilder) {
      this.childrenBuilder.add(categoryBuilder.apply(new Builder<>()).build());
      return this;
    }

    public CategoryConfigNode<C> build() {
      var n = this.node;

      Objects.requireNonNull(n.name);
      Objects.requireNonNull(n.title);
      n.children = this.childrenBuilder.build();
      this.node = null;
      this.childrenBuilder = null;
      return n;
    }
  }
}
