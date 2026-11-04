/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.core;

import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public interface TypedInstance<T> {
  public Holder<T> typeHolder();

  public default Stream<TagKey<T>> tags() {
    return this.typeHolder().tags();
  }

  public default boolean is(TagKey<T> tag) {
    return this.typeHolder().is(tag);
  }

  public default boolean is(HolderSet<T> set) {
    return set.contains(this.typeHolder());
  }

  public default boolean is(T rawType) {
    return this.typeHolder().value() == rawType;
  }

  public default boolean is(Holder<T> type) {
    return this.typeHolder() == type;
  }

  public default boolean is(ResourceKey<T> type) {
    return this.typeHolder().is(type);
  }
}
