/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.GpuFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public record VertexFormatElement(int id, int index, GpuFormat format) {
    public static final int MAX_COUNT = 32;
    private static final @Nullable VertexFormatElement[] BY_ID = new VertexFormatElement[32];
    private static final List<VertexFormatElement> ELEMENTS = new ArrayList<VertexFormatElement>(32);
    public static final VertexFormatElement POSITION = VertexFormatElement.register(0, 0, GpuFormat.RGB32_FLOAT);
    public static final VertexFormatElement COLOR = VertexFormatElement.register(1, 0, GpuFormat.RGBA8_UNORM);
    public static final VertexFormatElement UV0;
    public static final VertexFormatElement UV;
    public static final VertexFormatElement UV1;
    public static final VertexFormatElement UV2;
    public static final VertexFormatElement NORMAL;
    public static final VertexFormatElement LINE_WIDTH;

    public VertexFormatElement {
        if (id < 0 || id >= BY_ID.length) {
            throw new IllegalArgumentException("Element ID must be in range [0; " + BY_ID.length + ")");
        }
    }

    public static VertexFormatElement register(int id, int index, GpuFormat format) {
        VertexFormatElement element = new VertexFormatElement(id, index, format);
        if (BY_ID[id] != null) {
            throw new IllegalArgumentException("Duplicate element registration for: " + id);
        }
        VertexFormatElement.BY_ID[id] = element;
        ELEMENTS.add(element);
        return element;
    }

    @Override
    public String toString() {
        return String.valueOf((Object)this.format) + " (" + this.id + ")";
    }

    public int mask() {
        return 1 << this.id;
    }

    public int byteSize() {
        return this.format.pixelSize();
    }

    public static @Nullable VertexFormatElement byId(int id) {
        return BY_ID[id];
    }

    public static Stream<VertexFormatElement> elementsFromMask(int mask) {
        return ELEMENTS.stream().filter(element -> (mask & element.mask()) != 0);
    }

    static {
        UV = UV0 = VertexFormatElement.register(2, 0, GpuFormat.RG32_FLOAT);
        UV1 = VertexFormatElement.register(3, 1, GpuFormat.RG16_SINT);
        UV2 = VertexFormatElement.register(4, 2, GpuFormat.RG16_SINT);
        NORMAL = VertexFormatElement.register(5, 0, GpuFormat.RGBA8_SNORM);
        LINE_WIDTH = VertexFormatElement.register(6, 0, GpuFormat.R32_FLOAT);
    }
}

