/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.color.block;

import java.util.Set;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public interface BlockTintSource {
  public int color(BlockState var1);

  public default int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
    return this.color(state);
  }

  public default int colorAsTerrainParticle(
      BlockState state, BlockAndTintGetter level, BlockPos pos) {
    return this.colorInWorld(state, level, pos);
  }

  public default Set<Property<?>> relevantProperties() {
    return Set.of();
  }
}
