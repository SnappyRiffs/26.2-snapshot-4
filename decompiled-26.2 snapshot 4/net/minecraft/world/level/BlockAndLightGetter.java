/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.lighting.LevelLightEngine;

public interface BlockAndLightGetter extends BlockGetter {
  public LevelLightEngine getLightEngine();

  public default int getBrightness(LightLayer layer, BlockPos pos) {
    return this.getLightEngine().getLayerListener(layer).getLightValue(pos);
  }

  public default int getRawBrightness(BlockPos pos, int darkening) {
    return this.getLightEngine().getRawBrightness(pos, darkening);
  }

  public default boolean canSeeSky(BlockPos pos) {
    return this.getBrightness(LightLayer.SKY, pos) >= 15;
  }
}
