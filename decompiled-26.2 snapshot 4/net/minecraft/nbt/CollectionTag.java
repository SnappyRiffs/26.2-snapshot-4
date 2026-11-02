/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public sealed interface CollectionTag extends Tag, Iterable<Tag>
    permits ListTag, ByteArrayTag, IntArrayTag, LongArrayTag {
  public void clear();

  public boolean setTag(int var1, Tag var2);

  public boolean addTag(int var1, Tag var2);

  public Tag remove(int var1);

  public Tag get(int var1);

  public int size();

  public default boolean isEmpty() {
    return this.size() == 0;
  }

  @Override
  public default Iterator<Tag> iterator() {
    return new Iterator<Tag>(this) {
      private int index;
      final /* synthetic */ CollectionTag this$0;

      {
        CollectionTag collectionTag = this$0;
        Objects.requireNonNull(collectionTag);
        this.this$0 = collectionTag;
      }

      @Override
      public boolean hasNext() {
        return this.index < this.this$0.size();
      }

      @Override
      public Tag next() {
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        }
        return this.this$0.get(this.index++);
      }
    };
  }

  public default Stream<Tag> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }
}
