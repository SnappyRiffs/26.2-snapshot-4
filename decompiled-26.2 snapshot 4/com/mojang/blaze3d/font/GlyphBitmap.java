/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.blaze3d.font;

import com.mojang.blaze3d.textures.GpuTexture;

public interface GlyphBitmap {
  public int getPixelWidth();

  public int getPixelHeight();

  public void upload(int var1, int var2, GpuTexture var3);

  public boolean isColored();

  public float getOversample();

  public default float getLeft() {
    return this.getBearingLeft();
  }

  public default float getRight() {
    return this.getLeft() + (float) this.getPixelWidth() / this.getOversample();
  }

  public default float getTop() {
    return 7.0f - this.getBearingTop();
  }

  public default float getBottom() {
    return this.getTop() + (float) this.getPixelHeight() / this.getOversample();
  }

  public default float getBearingLeft() {
    return 0.0f;
  }

  public default float getBearingTop() {
    return 7.0f;
  }
}
