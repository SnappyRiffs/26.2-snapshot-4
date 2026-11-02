/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class RuleProcessor implements StructureProcessor {
  public static final MapCodec<RuleProcessor> MAP_CODEC =
      RecordCodecBuilder.mapCodec(
          i ->
              i.group((App) ProcessorRule.CODEC.listOf().fieldOf("rules").forGetter(p -> p.rules))
                  .apply((Applicative) i, RuleProcessor::new));
  private final ImmutableList<ProcessorRule> rules;

  public RuleProcessor(List<? extends ProcessorRule> rules) {
    this.rules = ImmutableList.copyOf(rules);
  }

  @Override
  public @Nullable StructureTemplate.StructureBlockInfo processBlock(
      LevelReader level,
      BlockPos targetPosition,
      BlockPos referencePos,
      StructureTemplate.StructureBlockInfo originalBlockInfo,
      StructureTemplate.StructureBlockInfo processedBlockInfo,
      StructurePlaceSettings settings) {
    RandomSource random = RandomSource.create(Mth.getSeed(processedBlockInfo.pos()));
    BlockState locState = level.getBlockState(processedBlockInfo.pos());
    for (ProcessorRule rule : this.rules) {
      if (!rule.test(
          processedBlockInfo.state(),
          locState,
          originalBlockInfo.pos(),
          processedBlockInfo.pos(),
          referencePos,
          random)) continue;
      return new StructureTemplate.StructureBlockInfo(
          processedBlockInfo.pos(),
          rule.getOutputState(),
          rule.getOutputTag(random, processedBlockInfo.nbt()));
    }
    return processedBlockInfo;
  }

  public MapCodec<RuleProcessor> codec() {
    return MAP_CODEC;
  }
}
