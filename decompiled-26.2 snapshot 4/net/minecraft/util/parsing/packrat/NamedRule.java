/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.parsing.packrat;

public interface NamedRule<S, T> {
  public Atom<T> name();

  public Rule<S, T> value();
}
