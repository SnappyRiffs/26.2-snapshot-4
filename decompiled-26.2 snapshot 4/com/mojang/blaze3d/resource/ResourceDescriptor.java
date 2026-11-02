/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.blaze3d.resource;

public interface ResourceDescriptor<T> {
  public T allocate();

  public default void prepare(T resource) {}

  public void free(T var1);

  public default boolean canUsePhysicalResource(ResourceDescriptor<?> other) {
    return this.equals(other);
  }
}
