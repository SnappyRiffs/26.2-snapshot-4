/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public interface FeatureConfiguration {
  public static final NoneFeatureConfiguration NONE = NoneFeatureConfiguration.INSTANCE;

  public default Stream<Holder<ConfiguredFeature<?, ?>>> getSubFeatures() {
    return Stream.empty();
  }
}
