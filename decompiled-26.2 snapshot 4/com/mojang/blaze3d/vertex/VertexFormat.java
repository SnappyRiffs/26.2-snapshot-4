/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.Mth;

public class VertexFormat {
  public static final int UNKNOWN_ELEMENT = -1;
  private static final int VERTEX_ALIGNMENT = 4;
  private final List<VertexFormatElement> elements;
  private final List<String> names;
  private final int vertexSize;
  private final int elementsMask;
  private final int[] offsetsByElement = new int[32];

  private VertexFormat(
      List<VertexFormatElement> elements, List<String> names, IntList offsets, int vertexSize) {
    this.elements = elements;
    this.names = names;
    this.vertexSize = vertexSize;
    this.elementsMask =
        elements.stream()
            .mapToInt(VertexFormatElement::mask)
            .reduce(0, (left, right) -> left | right);
    for (int id = 0; id < this.offsetsByElement.length; ++id) {
      VertexFormatElement element = VertexFormatElement.byId(id);
      int index = element != null ? elements.indexOf(element) : -1;
      this.offsetsByElement[id] = index != -1 ? offsets.getInt(index) : -1;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public String toString() {
    return "VertexFormat" + String.valueOf(this.names);
  }

  public int getVertexSize() {
    return this.vertexSize;
  }

  public List<VertexFormatElement> getElements() {
    return this.elements;
  }

  public List<String> getElementAttributeNames() {
    return this.names;
  }

  public int[] getOffsetsByElement() {
    return this.offsetsByElement;
  }

  public int getOffset(VertexFormatElement element) {
    return this.offsetsByElement[element.id()];
  }

  public boolean contains(VertexFormatElement element) {
    return (this.elementsMask & element.mask()) != 0;
  }

  public int getElementsMask() {
    return this.elementsMask;
  }

  public String getElementName(VertexFormatElement element) {
    int index = this.elements.indexOf(element);
    if (index == -1) {
      throw new IllegalArgumentException(String.valueOf(element) + " is not contained in format");
    }
    return this.names.get(index);
  }

  /*
   * Enabled force condition propagation
   * Lifted jumps to return sites
   */
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof VertexFormat)) return false;
    VertexFormat format = (VertexFormat) o;
    if (this.elementsMask != format.elementsMask) return false;
    if (this.vertexSize != format.vertexSize) return false;
    if (!this.names.equals(format.names)) return false;
    if (!Arrays.equals(this.offsetsByElement, format.offsetsByElement)) return false;
    return true;
  }

  public int hashCode() {
    return this.elementsMask * 31 + Arrays.hashCode(this.offsetsByElement);
  }

  public static class Builder {
    private final ImmutableMap.Builder<String, VertexFormatElement> elements =
        ImmutableMap.builder();
    private final IntList offsets = new IntArrayList();
    private int offset;

    private Builder() {}

    public Builder add(String name, VertexFormatElement element) {
      if (!Mth.isMultipleOf(this.offset, element.format().byteAlignment())) {
        throw new IllegalArgumentException(
            name
                + " is not aligned to "
                + element.format().byteAlignment()
                + " as required by "
                + String.valueOf((Object) element.format()));
      }
      this.elements.put((Object) name, (Object) element);
      this.offsets.add(this.offset);
      this.offset += element.byteSize();
      return this;
    }

    public Builder padding(int bytes) {
      this.offset += bytes;
      return this;
    }

    public VertexFormat build() {
      ImmutableMap elementMap = this.elements.buildOrThrow();
      ImmutableList elements = elementMap.values().asList();
      ImmutableList names = elementMap.keySet().asList();
      int vertexSize = this.offset;
      if (!Mth.isMultipleOf(vertexSize, 4)) {
        throw new IllegalStateException("Vertex size must be a multiple of 4, was " + vertexSize);
      }
      return new VertexFormat(
          (List<VertexFormatElement>) elements, (List<String>) names, this.offsets, vertexSize);
    }
  }

  public static enum Mode {
    LINES(2, 2, false),
    DEBUG_LINES(2, 2, false),
    DEBUG_LINE_STRIP(2, 1, true),
    POINTS(1, 1, false),
    TRIANGLES(3, 3, false),
    TRIANGLE_STRIP(3, 1, true),
    TRIANGLE_FAN(3, 1, true),
    QUADS(4, 4, false);

    public final int primitiveLength;
    public final int primitiveStride;
    public final boolean connectedPrimitives;

    private Mode(int primitiveLength, int primitiveStride, boolean connectedPrimitives) {
      this.primitiveLength = primitiveLength;
      this.primitiveStride = primitiveStride;
      this.connectedPrimitives = connectedPrimitives;
    }

    public int indexCount(int vertexCount) {
      return switch (this.ordinal()) {
        case 1, 2, 3, 4, 5, 6 -> vertexCount;
        case 0, 7 -> vertexCount / 4 * 6;
        default -> 0;
      };
    }
  }

  public static enum IndexType {
    SHORT(2),
    INT(4);

    public final int bytes;

    private IndexType(int bytes) {
      this.bytes = bytes;
    }

    public static IndexType least(int length) {
      if ((length & 0xFFFF0000) != 0) {
        return INT;
      }
      return SHORT;
    }
  }
}
