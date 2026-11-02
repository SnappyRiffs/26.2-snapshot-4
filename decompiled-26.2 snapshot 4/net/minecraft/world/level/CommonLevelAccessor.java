/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.world.level;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public interface CommonLevelAccessor extends LevelReader, LevelSimulatedRW, EntityGetter {
  @Override
  public default <T extends BlockEntity> Optional<T> getBlockEntity(
      BlockPos pos, BlockEntityType<T> type) {
    return LevelReader.super.getBlockEntity(pos, type);
  }

  @Override
  public default List<VoxelShape> getEntityCollisions(@Nullable Entity source, AABB testArea) {
    return EntityGetter.super.getEntityCollisions(source, testArea);
  }

  @Override
  public default boolean isUnobstructed(@Nullable Entity source, VoxelShape shape) {
    return EntityGetter.super.isUnobstructed(source, shape);
  }

  @Override
  public default BlockPos getHeightmapPos(Heightmap.Types type, BlockPos pos) {
    return LevelReader.super.getHeightmapPos(type, pos);
  }
}
