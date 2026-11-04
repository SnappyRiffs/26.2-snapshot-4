/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.core.component;

import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public interface DataComponentHolder extends DataComponentGetter {
  public DataComponentMap getComponents();

  @Override
  public default <T> @Nullable T get(DataComponentType<? extends T> type) {
    return this.getComponents().get(type);
  }

  public default <T> Stream<T> getAllOfType(Class<? extends T> valueClass) {
    return this.getComponents().stream()
        .map(TypedDataComponent::value)
        .filter(value -> valueClass.isAssignableFrom(value.getClass()))
        .map(value -> value);
  }

  @Override
  public default <T> T getOrDefault(DataComponentType<? extends T> type, T defaultValue) {
    return this.getComponents().getOrDefault(type, defaultValue);
  }

  public default boolean has(DataComponentType<?> type) {
    return this.getComponents().has(type);
  }
}
