/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FoliagePlacerType<P extends FoliagePlacer> {
  public static final FoliagePlacerType<BlobFoliagePlacer> BLOB_FOLIAGE_PLACER =
      FoliagePlacerType.register("blob_foliage_placer", BlobFoliagePlacer.CODEC);
  public static final FoliagePlacerType<SpruceFoliagePlacer> SPRUCE_FOLIAGE_PLACER =
      FoliagePlacerType.register("spruce_foliage_placer", SpruceFoliagePlacer.CODEC);
  public static final FoliagePlacerType<PineFoliagePlacer> PINE_FOLIAGE_PLACER =
      FoliagePlacerType.register("pine_foliage_placer", PineFoliagePlacer.CODEC);
  public static final FoliagePlacerType<AcaciaFoliagePlacer> ACACIA_FOLIAGE_PLACER =
      FoliagePlacerType.register("acacia_foliage_placer", AcaciaFoliagePlacer.CODEC);
  public static final FoliagePlacerType<BushFoliagePlacer> BUSH_FOLIAGE_PLACER =
      FoliagePlacerType.register("bush_foliage_placer", BushFoliagePlacer.CODEC);
  public static final FoliagePlacerType<FancyFoliagePlacer> FANCY_FOLIAGE_PLACER =
      FoliagePlacerType.register("fancy_foliage_placer", FancyFoliagePlacer.CODEC);
  public static final FoliagePlacerType<MegaJungleFoliagePlacer> MEGA_JUNGLE_FOLIAGE_PLACER =
      FoliagePlacerType.register("jungle_foliage_placer", MegaJungleFoliagePlacer.CODEC);
  public static final FoliagePlacerType<MegaPineFoliagePlacer> MEGA_PINE_FOLIAGE_PLACER =
      FoliagePlacerType.register("mega_pine_foliage_placer", MegaPineFoliagePlacer.CODEC);
  public static final FoliagePlacerType<DarkOakFoliagePlacer> DARK_OAK_FOLIAGE_PLACER =
      FoliagePlacerType.register("dark_oak_foliage_placer", DarkOakFoliagePlacer.CODEC);
  public static final FoliagePlacerType<RandomSpreadFoliagePlacer> RANDOM_SPREAD_FOLIAGE_PLACER =
      FoliagePlacerType.register("random_spread_foliage_placer", RandomSpreadFoliagePlacer.CODEC);
  public static final FoliagePlacerType<CherryFoliagePlacer> CHERRY_FOLIAGE_PLACER =
      FoliagePlacerType.register("cherry_foliage_placer", CherryFoliagePlacer.CODEC);
  private final MapCodec<P> codec;

  private static <P extends FoliagePlacer> FoliagePlacerType<P> register(
      String name, MapCodec<P> codec) {
    return Registry.register(
        BuiltInRegistries.FOLIAGE_PLACER_TYPE, name, new FoliagePlacerType<P>(codec));
  }

  private FoliagePlacerType(MapCodec<P> codec) {
    this.codec = codec;
  }

  public MapCodec<P> codec() {
    return this.codec;
  }
}
