/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.gametest.framework;

import java.util.stream.Stream;
import net.minecraft.core.Holder;

@FunctionalInterface
public interface TestInstanceFinder {
  public Stream<Holder.Reference<GameTestInstance>> findTests();
}
