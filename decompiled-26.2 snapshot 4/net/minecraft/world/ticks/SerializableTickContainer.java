/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.ticks;

import java.util.List;

public interface SerializableTickContainer<T> {
  public List<SavedTick<T>> pack(long var1);
}
