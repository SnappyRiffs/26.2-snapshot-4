/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.core;

public interface HolderOwner<T> {
  public default boolean canSerializeIn(HolderOwner<T> context) {
    return context == this;
  }
}
