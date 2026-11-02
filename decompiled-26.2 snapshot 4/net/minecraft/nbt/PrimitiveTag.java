/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

public sealed interface PrimitiveTag extends Tag permits NumericTag, StringTag {
  @Override
  public default Tag copy() {
    return this;
  }
}
