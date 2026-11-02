/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util;

import java.io.Serializable;
import java.util.Deque;
import java.util.List;
import java.util.RandomAccess;
import org.jspecify.annotations.Nullable;

public interface ListAndDeque<T> extends List<T>, RandomAccess, Cloneable, Serializable, Deque<T> {
  @Override
  public ListAndDeque<T> reversed();

  @Override
  public T getFirst();

  @Override
  public T getLast();

  @Override
  public void addFirst(T var1);

  @Override
  public void addLast(T var1);

  @Override
  public T removeFirst();

  @Override
  public T removeLast();

  @Override
  public default boolean offer(T value) {
    return this.offerLast(value);
  }

  @Override
  public default T remove() {
    return this.removeFirst();
  }

  @Override
  public default @Nullable T poll() {
    return (T) this.pollFirst();
  }

  @Override
  public default T element() {
    return this.getFirst();
  }

  @Override
  public default @Nullable T peek() {
    return (T) this.peekFirst();
  }

  @Override
  public default void push(T value) {
    this.addFirst(value);
  }

  @Override
  public default T pop() {
    return this.removeFirst();
  }
}
