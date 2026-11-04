/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.level.biome;

import net.minecraft.core.Holder;

public interface BiomeResolver {
  public Holder<Biome> getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4);
}
