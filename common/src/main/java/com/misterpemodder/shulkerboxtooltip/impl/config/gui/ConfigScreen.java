package com.misterpemodder.shulkerboxtooltip.impl.config.gui;

import com.misterpemodder.shulkerboxtooltip.impl.PluginManager;
import com.misterpemodder.shulkerboxtooltip.impl.tree.RootConfigNode;
import com.mojang.blaze3d.opengl.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public final class ConfigScreen<C> extends Screen {
  private final RootConfigNode<C> root;
  private final C config;
  private final Consumer<C> onSave;
  private final Screen previous;
  private final HeaderAndFooterLayout layout;
  private final TabManager tabManager;
  private TabNavigationBar tabNavigationBar;

  private List<ConfigCategoryTab<C>> tabs;
  private Button quitButton;
  private Button saveAndQuitButton;

  private static final Component CANCEL_LABEL = CommonComponents.GUI_CANCEL;
  private static final Component QUIT_UNSAVED_LABEL = Component.translatable("shulkerboxtooltip.config.quit.unsaved");
  private static final Component SAVE_LABEL = Component.translatable("shulkerboxtooltip.config.save");
  private static final Component CANNOT_SAVE_LABEL = Component.translatable("shulkerboxtooltip.config.cannot_save");
  private static final Component QUIT_CONFIRM_LABEL = Component.translatable("shulkerboxtooltip.config.quit.confirm");
  private static final Component QUIT_CONFIRM_TITLE = Component.translatable(
      "shulkerboxtooltip.config.quit.confirm.title");
  private static final Component QUIT_CONFIRM_WARNING = Component.translatable(
      "shulkerboxtooltip.config.quit.confirm.warning");
  private static final Component RESTART_REQUIRED_LABEL = Component.translatable(
      "shulkerboxtooltip.config.restart_required");
  private static final Component RESTART_REQUIRED_TITLE = Component.translatable(
      "shulkerboxtooltip.config.restart_required.title");
  private static final Component EXIT_MINECRAFT_LABEL = Component.translatable(
      "shulkerboxtooltip.config.exit_minecraft");
  private static final Component IGNORE_RESTART_LABEL = Component.translatable(
      "shulkerboxtooltip.config.ignore_restart");

  public ConfigScreen(Screen previous, RootConfigNode<C> root, C config, Consumer<C> onSave) {
    super(root.getTitle());
    PluginManager.loadColors();
    this.root = root;
    this.config = config;
    this.onSave = onSave;
    this.previous = previous;
    this.layout = new HeaderAndFooterLayout(this, 24, 33);
    this.tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);
    this.tabs = List.of();
  }

  @Override
  protected void init() {
    this.root.resetToActive(this.config);

    var tabNavigationBarBuilder = TabNavigationBar.builder(this.tabManager, this.width);

    this.tabs = new ArrayList<>();
    for (var category : this.root.getCategories()) {
      var tab = new ConfigCategoryTab<>(this, category, this.config);
      tabNavigationBarBuilder.addTabs(tab);
      this.tabs.add(tab);
    }
    this.tabNavigationBar = tabNavigationBarBuilder.build();
    this.initTabs(this.tabNavigationBar);
    this.addRenderableWidget(this.tabNavigationBar);

    LinearLayout footerLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
    this.quitButton = footerLayout.addChild(Button.builder(this.getQuitLabel(), b -> this.onClose())
        .width(200)
        .build());
    this.saveAndQuitButton = footerLayout.addChild(Button.builder(this.getSaveLabel(), b -> this.saveAndQuit())
        .width(200)
        .build());
    this.saveAndQuitButton.active = !this.root.isActiveValue(this.config) && this.root.validate(this.config) == null;

    this.layout.visitWidgets(abstractWidget -> {
      abstractWidget.setTabOrderGroup(1);
      this.addRenderableWidget(abstractWidget);
    });
    this.tabNavigationBar.selectTab(0, false);
    this.repositionElements();
  }

  private void initTabs(TabNavigationBar bar) {
    int i = 0;
    for (var child : bar.children()) {
      if (child instanceof TabButton tabButton) {
        this.tabs.get(i).setTabButton(tabButton);
        ++i;
      }
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int i, int j, float f) {
    super.render(guiGraphics, i, j, f);
    GlStateManager._enableBlend();
    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, this.height - this.getFooterHeight() - 2,
        0.0F, 0.0F, this.width, 2, 32, 2);
    GlStateManager._disableBlend();
  }

  @Override
  protected void repositionElements() {
    this.refresh();

    if (this.tabNavigationBar != null) {
      this.tabNavigationBar.setWidth(this.width);
      this.tabNavigationBar.arrangeElements();
      int i = this.tabNavigationBar.getRectangle().bottom();
      ScreenRectangle screenRectangle = new ScreenRectangle(0, i, this.width,
          this.height - this.layout.getFooterHeight() - i);
      this.tabManager.setTabArea(screenRectangle);
      this.layout.setHeaderHeight(i);
      this.layout.arrangeElements();
    }
  }

  @Override
  public boolean keyPressed(KeyEvent event) {
    return
        (this.tabManager.getCurrentTab() != null && ((ConfigCategoryTab<?>) this.tabManager.getCurrentTab()).keyPressed(
            event)) || (this.tabNavigationBar.keyPressed(event)) || (super.keyPressed(event));
  }

  @Override
  public void onClose() {
    if (this.root.isActiveValue(this.config)) {
      // no changes, no need to confirm
      this.getMinecraft().setScreen(this.previous);
      return;
    }

    this.getMinecraft().setScreen(
        new ConfirmScreen(confirmed -> this.getMinecraft().setScreen(confirmed ? this.previous : this),
            QUIT_CONFIRM_TITLE, QUIT_CONFIRM_WARNING, QUIT_CONFIRM_LABEL, CANCEL_LABEL));
  }

  public void saveAndQuit() {
    var restartRequired = this.root.restartRequired(this.config);

    this.root.writeEditingToConfig(this.config);
    this.onSave.accept(this.config);

    if (restartRequired) {
      this.getMinecraft().setScreen(new ConfirmScreen(confirmed -> {
        if (confirmed) {
          this.getMinecraft().stop();
        } else {
          this.getMinecraft().setScreen(this.previous);
        }
      }, RESTART_REQUIRED_TITLE, RESTART_REQUIRED_LABEL, EXIT_MINECRAFT_LABEL, IGNORE_RESTART_LABEL));
    } else {
      this.getMinecraft().setScreen(this.previous);
    }
  }

  public Minecraft getMinecraft() {
    return Objects.requireNonNull(this.minecraft);
  }

  public int getHeaderHeight() {
    return this.layout.getHeaderHeight();
  }

  public int getFooterHeight() {
    return this.layout.getFooterHeight();
  }

  public void refresh() {
    this.tabs.forEach(ConfigCategoryTab::refresh);
    this.saveAndQuitButton.active = !this.root.isActiveValue(this.config) && this.root.validate(this.config) == null;
    this.quitButton.setMessage(this.getQuitLabel());
    this.saveAndQuitButton.setMessage(this.getSaveLabel());
  }

  private Component getQuitLabel() {
    return this.root.isActiveValue(this.config) ? CANCEL_LABEL : QUIT_UNSAVED_LABEL;
  }

  private Component getSaveLabel() {
    return this.root.validate(this.config) == null ? SAVE_LABEL : CANNOT_SAVE_LABEL;
  }
}
