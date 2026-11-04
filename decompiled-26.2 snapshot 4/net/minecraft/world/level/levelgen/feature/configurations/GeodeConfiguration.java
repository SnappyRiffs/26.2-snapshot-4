/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;

public class GeodeConfiguration implements FeatureConfiguration {
  public static final Codec<Double> CHANCE_RANGE = Codec.doubleRange((double) 0.0, (double) 1.0);
  public static final Codec<GeodeConfiguration> CODEC =
      RecordCodecBuilder.create(
          i ->
              i.group(
                      (App)
                          GeodeBlockSettings.CODEC
                              .fieldOf("blocks")
                              .forGetter(c -> c.geodeBlockSettings),
                      (App)
                          GeodeLayerSettings.CODEC
                              .fieldOf("layers")
                              .forGetter(c -> c.geodeLayerSettings),
                      (App)
                          GeodeCrackSettings.CODEC
                              .fieldOf("crack")
                              .forGetter(c -> c.geodeCrackSettings),
                      (App)
                          CHANCE_RANGE
                              .fieldOf("use_potential_placements_chance")
                              .orElse((Object) 0.35)
                              .forGetter(c -> c.usePotentialPlacementsChance),
                      (App)
                          CHANCE_RANGE
                              .fieldOf("use_alternate_layer0_chance")
                              .orElse((Object) 0.0)
                              .forGetter(c -> c.useAlternateLayer0Chance),
                      (App)
                          Codec.BOOL
                              .fieldOf("placements_require_layer0_alternate")
                              .orElse((Object) true)
                              .forGetter(c -> c.placementsRequireLayer0Alternate),
                      (App)
                          IntProviders.codec(1, 20)
                              .fieldOf("outer_wall_distance")
                              .orElse((Object) UniformInt.of(4, 5))
                              .forGetter(c -> c.outerWallDistance),
                      (App)
                          IntProviders.codec(1, 20)
                              .fieldOf("distribution_points")
                              .orElse((Object) UniformInt.of(3, 4))
                              .forGetter(c -> c.distributionPoints),
                      (App)
                          IntProviders.codec(0, 10)
                              .fieldOf("point_offset")
                              .orElse((Object) UniformInt.of(1, 2))
                              .forGetter(c -> c.pointOffset),
                      (App)
                          Codec.INT
                              .fieldOf("min_gen_offset")
                              .orElse((Object) - 16)
                              .forGetter(c -> c.minGenOffset),
                      (App)
                          Codec.INT
                              .fieldOf("max_gen_offset")
                              .orElse((Object) 16)
                              .forGetter(c -> c.maxGenOffset),
                      (App)
                          CHANCE_RANGE
                              .fieldOf("noise_multiplier")
                              .orElse((Object) 0.05)
                              .forGetter(c -> c.noiseMultiplier),
                      (App)
                          Codec.INT
                              .fieldOf("invalid_blocks_threshold")
                              .forGetter(c -> c.invalidBlocksThreshold))
                  .apply((Applicative) i, GeodeConfiguration::new));
  public final GeodeBlockSettings geodeBlockSettings;
  public final GeodeLayerSettings geodeLayerSettings;
  public final GeodeCrackSettings geodeCrackSettings;
  public final double usePotentialPlacementsChance;
  public final double useAlternateLayer0Chance;
  public final boolean placementsRequireLayer0Alternate;
  public final IntProvider outerWallDistance;
  public final IntProvider distributionPoints;
  public final IntProvider pointOffset;
  public final int minGenOffset;
  public final int maxGenOffset;
  public final double noiseMultiplier;
  public final int invalidBlocksThreshold;

  public GeodeConfiguration(
      GeodeBlockSettings geodeBlockSettings,
      GeodeLayerSettings geodeLayerSettings,
      GeodeCrackSettings geodeCrackSettings,
      double usePotentialPlacementsChance,
      double useAlternateLayer0Chance,
      boolean placementsRequireLayer0Alternate,
      IntProvider outerWallDistance,
      IntProvider distributionPoints,
      IntProvider pointOffset,
      int minGenOffset,
      int maxGenOffset,
      double noiseMultiplier,
      int invalidBlocksThreshold) {
    this.geodeBlockSettings = geodeBlockSettings;
    this.geodeLayerSettings = geodeLayerSettings;
    this.geodeCrackSettings = geodeCrackSettings;
    this.usePotentialPlacementsChance = usePotentialPlacementsChance;
    this.useAlternateLayer0Chance = useAlternateLayer0Chance;
    this.placementsRequireLayer0Alternate = placementsRequireLayer0Alternate;
    this.outerWallDistance = outerWallDistance;
    this.distributionPoints = distributionPoints;
    this.pointOffset = pointOffset;
    this.minGenOffset = minGenOffset;
    this.maxGenOffset = maxGenOffset;
    this.noiseMultiplier = noiseMultiplier;
    this.invalidBlocksThreshold = invalidBlocksThreshold;
  }
}
