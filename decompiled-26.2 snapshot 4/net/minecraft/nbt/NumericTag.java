/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.util.Optional;

public sealed interface NumericTag extends PrimitiveTag
    permits ByteTag, ShortTag, IntTag, LongTag, FloatTag, DoubleTag {
  public byte byteValue();

  public short shortValue();

  public int intValue();

  public long longValue();

  public float floatValue();

  public double doubleValue();

  public Number box();

  @Override
  public default Optional<Number> asNumber() {
    return Optional.of(this.box());
  }

  @Override
  public default Optional<Byte> asByte() {
    return Optional.of(this.byteValue());
  }

  @Override
  public default Optional<Short> asShort() {
    return Optional.of(this.shortValue());
  }

  @Override
  public default Optional<Integer> asInt() {
    return Optional.of(this.intValue());
  }

  @Override
  public default Optional<Long> asLong() {
    return Optional.of(this.longValue());
  }

  @Override
  public default Optional<Float> asFloat() {
    return Optional.of(Float.valueOf(this.floatValue()));
  }

  @Override
  public default Optional<Double> asDouble() {
    return Optional.of(this.doubleValue());
  }

  @Override
  public default Optional<Boolean> asBoolean() {
    return Optional.of(this.byteValue() != 0);
  }
}
