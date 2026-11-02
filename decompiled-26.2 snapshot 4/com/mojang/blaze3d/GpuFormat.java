/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.blaze3d;

public enum GpuFormat {
    R8_UNORM(1, 1),
    R8_SNORM(1, 1),
    RG8_UNORM(2, 1),
    RG8_SNORM(2, 1),
    RGB8_UNORM(3, 1),
    RGB8_SNORM(3, 1),
    RGBA8_UNORM(4, 1),
    RGBA8_SNORM(4, 1),
    R16_UNORM(2, 2),
    R16_SNORM(2, 2),
    RG16_UNORM(4, 2),
    RG16_SNORM(4, 2),
    RGB16_UNORM(6, 2),
    RGB16_SNORM(6, 2),
    RGBA16_UNORM(8, 2),
    RGBA16_SNORM(8, 2),
    R8_UINT(1, 1),
    R8_SINT(1, 1),
    RG8_UINT(2, 1),
    RG8_SINT(2, 1),
    RGB8_UINT(3, 1),
    RGB8_SINT(3, 1),
    RGBA8_UINT(4, 1),
    RGBA8_SINT(4, 1),
    R16_UINT(2, 2),
    R16_SINT(2, 2),
    RG16_UINT(4, 2),
    RG16_SINT(4, 2),
    RGB16_UINT(6, 2),
    RGB16_SINT(6, 2),
    RGBA16_UINT(8, 2),
    RGBA16_SINT(8, 2),
    R32_UINT(4, 4),
    R32_SINT(4, 4),
    RG32_UINT(8, 4),
    RG32_SINT(8, 4),
    RGB32_UINT(12, 4),
    RGB32_SINT(12, 4),
    RGBA32_UINT(16, 4),
    RGBA32_SINT(16, 4),
    R16_FLOAT(2, 2),
    RG16_FLOAT(4, 2),
    RGB16_FLOAT(6, 2),
    RGBA16_FLOAT(8, 2),
    R32_FLOAT(4, 4),
    RG32_FLOAT(8, 4),
    RGB32_FLOAT(12, 4),
    RGBA32_FLOAT(16, 4),
    RGB10A2_UNORM(4, 1),
    RGB10A2_UINT(4, 1),
    RG11B10_FLOAT(4, 1),
    D32_FLOAT(4, 4),
    D32_FLOAT_S8_UINT(8, 1),
    D24_UNORM_S8_UINT(4, 1),
    D16_UNORM(2, 2),
    S8_UINT(1, 1);

    private final int pixelSize;
    private final int byteAlignment;

    private GpuFormat(int pixelSize, int byteAlignment) {
        this.pixelSize = pixelSize;
        this.byteAlignment = byteAlignment;
    }

    public int pixelSize() {
        return this.pixelSize;
    }

    public int byteAlignment() {
        return this.byteAlignment;
    }

    public boolean hasColorAspect() {
        return !this.hasDepthAspect() && !this.hasStencilAspect();
    }

    public boolean hasDepthAspect() {
        return this == D32_FLOAT || this == D32_FLOAT_S8_UINT || this == D24_UNORM_S8_UINT || this == D16_UNORM;
    }

    public boolean hasStencilAspect() {
        return this == S8_UINT || this == D32_FLOAT_S8_UINT || this == D24_UNORM_S8_UINT;
    }
}

