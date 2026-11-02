/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.core.component;

import org.jspecify.annotations.Nullable;

public interface DataComponentGetter {
  public <T> @Nullable T get(DataComponentType<? extends T> var1);

  public default <T> T getOrDefault(DataComponentType<? extends T> type, T defaultValue) {
    T value = this.get(type);
    return value != null ? value : defaultValue;
  }

  public default <T> @Nullable TypedDataComponent<T> getTyped(DataComponentType<T> type) {
    T value = this.get(type);
    return value != null ? new TypedDataComponent<T>(type, value) : null;
  }
}
