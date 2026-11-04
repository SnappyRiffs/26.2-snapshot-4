/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;

public interface StructureProcessor {
  public default @Nullable StructureTemplate.StructureBlockInfo processBlock(
      LevelReader level,
      BlockPos targetPosition,
      BlockPos referencePos,
      StructureTemplate.StructureBlockInfo originalBlockInfo,
      StructureTemplate.StructureBlockInfo processedBlockInfo,
      StructurePlaceSettings settings) {
    return processedBlockInfo;
  }

  public MapCodec<? extends StructureProcessor> codec();

  public default List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
      ServerLevelAccessor level,
      BlockPos position,
      BlockPos referencePos,
      List<StructureTemplate.StructureBlockInfo> originalBlockInfoList,
      List<StructureTemplate.StructureBlockInfo> processedBlockInfoList,
      StructurePlaceSettings settings) {
    return processedBlockInfoList;
  }
}
