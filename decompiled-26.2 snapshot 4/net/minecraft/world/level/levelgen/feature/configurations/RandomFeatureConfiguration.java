/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RandomFeatureConfiguration
implements FeatureConfiguration {
    public static final Codec<RandomFeatureConfiguration> CODEC = RecordCodecBuilder.create(i -> i.apply2(RandomFeatureConfiguration::new, (App)WeightedPlacedFeature.CODEC.listOf().fieldOf("features").forGetter(c -> c.features), (App)PlacedFeature.CODEC.fieldOf("default").forGetter(c -> c.defaultFeature)));
    public final List<WeightedPlacedFeature> features;
    public final Holder<PlacedFeature> defaultFeature;

    public RandomFeatureConfiguration(List<WeightedPlacedFeature> features, Holder<PlacedFeature> defaultFeature) {
        this.features = features;
        this.defaultFeature = defaultFeature;
    }

    @Override
    public Stream<Holder<ConfiguredFeature<?, ?>>> getSubFeatures() {
        return Stream.concat(this.features.stream().flatMap(weighted -> weighted.feature.value().getFeatures()), this.defaultFeature.value().getFeatures());
    }
}

