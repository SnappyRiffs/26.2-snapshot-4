/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.blaze3d.font;

public interface GlyphInfo {
  public float getAdvance();

  public default float getAdvance(boolean bold) {
    return this.getAdvance() + (bold ? this.getBoldOffset() : 0.0f);
  }

  public default float getBoldOffset() {
    return 1.0f;
  }

  public default float getShadowOffset() {
    return 1.0f;
  }

  public static GlyphInfo simple(float advance) {
    return () -> advance;
  }
}
