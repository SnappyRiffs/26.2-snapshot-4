/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.components.events;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import org.jspecify.annotations.Nullable;

public interface GuiEventListener extends TabOrderedElement {
  public default void mouseMoved(double x, double y) {}

  public default boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
    return false;
  }

  public default boolean mouseReleased(MouseButtonEvent event) {
    return false;
  }

  public default boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
    return false;
  }

  public default boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
    return false;
  }

  public default boolean keyPressed(KeyEvent event) {
    return false;
  }

  public default boolean keyReleased(KeyEvent event) {
    return false;
  }

  public default boolean charTyped(CharacterEvent event) {
    return false;
  }

  public default boolean preeditUpdated(@Nullable PreeditEvent event) {
    return false;
  }

  public default @Nullable ComponentPath nextFocusPath(FocusNavigationEvent navigationEvent) {
    return null;
  }

  public default boolean isMouseOver(double mouseX, double mouseY) {
    return false;
  }

  public void setFocused(boolean var1);

  public boolean isFocused();

  public default boolean shouldTakeFocusAfterInteraction() {
    return true;
  }

  public default @Nullable ComponentPath getCurrentFocusPath() {
    if (this.isFocused()) {
      return ComponentPath.leaf(this);
    }
    return null;
  }

  public default ScreenRectangle getRectangle() {
    return ScreenRectangle.empty();
  }

  public default ScreenRectangle getBorderForArrowNavigation(ScreenDirection opposite) {
    return this.getRectangle().getBorder(opposite);
  }
}
