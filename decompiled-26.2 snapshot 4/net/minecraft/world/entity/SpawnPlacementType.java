/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jspecify.annotations.Nullable;

public interface SpawnPlacementType {
  public boolean isSpawnPositionOk(LevelReader var1, BlockPos var2, @Nullable EntityType<?> var3);

  public default BlockPos adjustSpawnPosition(LevelReader level, BlockPos candidate) {
    return candidate;
  }
}
