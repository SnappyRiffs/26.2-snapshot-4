/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.syncher;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface EntityDataSerializer<T> {
  public StreamCodec<? super RegistryFriendlyByteBuf, T> codec();

  public default EntityDataAccessor<T> createAccessor(int id) {
    return new EntityDataAccessor(id, this);
  }

  public T copy(T var1);

  public static <T> EntityDataSerializer<T> forValueType(
      StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
    return () -> codec;
  }

  public static interface ForValueType<T> extends EntityDataSerializer<T> {
    @Override
    public default T copy(T value) {
      return value;
    }
  }
}
