/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.jspecify.annotations.Nullable
 */
package com.mojang.blaze3d.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.gui.font.FontOption;
import org.jspecify.annotations.Nullable;

public interface GlyphProvider extends AutoCloseable {
  public static final float BASELINE = 7.0f;

  @Override
  public default void close() {}

  public default @Nullable UnbakedGlyph getGlyph(int codepoint) {
    return null;
  }

  public IntSet getSupportedGlyphs();

  public record Conditional(GlyphProvider provider, FontOption.Filter filter)
      implements AutoCloseable {
    @Override
    public void close() {
      this.provider.close();
    }
  }
}
