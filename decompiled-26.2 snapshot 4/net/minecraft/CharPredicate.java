/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft;

import java.util.Objects;

@FunctionalInterface
public interface CharPredicate {
  public boolean test(char var1);

  public default CharPredicate and(CharPredicate other) {
    Objects.requireNonNull(other);
    return value -> this.test(value) && other.test(value);
  }

  public default CharPredicate negate() {
    return value -> !this.test(value);
  }

  public default CharPredicate or(CharPredicate other) {
    Objects.requireNonNull(other);
    return value -> this.test(value) || other.test(value);
  }
}
