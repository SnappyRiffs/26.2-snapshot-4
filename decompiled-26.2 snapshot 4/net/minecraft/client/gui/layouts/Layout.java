/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;

public interface Layout extends LayoutElement {
  public void visitChildren(Consumer<LayoutElement> var1);

  @Override
  public default void visitWidgets(Consumer<AbstractWidget> widgetVisitor) {
    this.visitChildren(child -> child.visitWidgets(widgetVisitor));
  }

  public default void arrangeElements() {
    this.visitChildren(
        child -> {
          if (child instanceof Layout) {
            Layout layout = (Layout) child;
            layout.arrangeElements();
          }
        });
  }
}
