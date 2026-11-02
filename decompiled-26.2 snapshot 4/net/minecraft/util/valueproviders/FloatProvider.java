/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.util.valueproviders;

import com.mojang.serialization.MapCodec;

public interface FloatProvider extends SampledFloat {
  public float min();

  public float max();

  public MapCodec<? extends FloatProvider> codec();
}
