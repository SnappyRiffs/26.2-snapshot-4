/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.level.storage.loot.entries;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;

@FunctionalInterface
interface ComposableEntryContainer {
  public static final ComposableEntryContainer ALWAYS_FALSE = (context, output) -> false;
  public static final ComposableEntryContainer ALWAYS_TRUE = (context, output) -> true;

  public boolean expand(LootContext var1, Consumer<LootPoolEntry> var2);

  public default ComposableEntryContainer and(ComposableEntryContainer other) {
    Objects.requireNonNull(other);
    return (context, output) -> this.expand(context, output) && other.expand(context, output);
  }

  public default ComposableEntryContainer or(ComposableEntryContainer other) {
    Objects.requireNonNull(other);
    return (context, output) -> this.expand(context, output) || other.expand(context, output);
  }
}
