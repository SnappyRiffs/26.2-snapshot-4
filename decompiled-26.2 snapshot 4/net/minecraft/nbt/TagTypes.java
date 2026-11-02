/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

public class TagTypes {
  private static final TagType<?>[] TYPES =
      new TagType[] {
        EndTag.TYPE,
        ByteTag.TYPE,
        ShortTag.TYPE,
        IntTag.TYPE,
        LongTag.TYPE,
        FloatTag.TYPE,
        DoubleTag.TYPE,
        ByteArrayTag.TYPE,
        StringTag.TYPE,
        ListTag.TYPE,
        CompoundTag.TYPE,
        IntArrayTag.TYPE,
        LongArrayTag.TYPE
      };

  public static TagType<?> getType(int typeId) {
    if (typeId < 0 || typeId >= TYPES.length) {
      return TagType.createInvalid(typeId);
    }
    return TYPES[typeId];
  }
}
