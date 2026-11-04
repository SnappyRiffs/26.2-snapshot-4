/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public interface LevelHeightAccessor {
  public int getHeight();

  public int getMinY();

  public default int getMaxY() {
    return this.getMinY() + this.getHeight() - 1;
  }

  public default int getSectionsCount() {
    return this.getMaxSectionY() - this.getMinSectionY() + 1;
  }

  public default int getMinSectionY() {
    return SectionPos.blockToSectionCoord(this.getMinY());
  }

  public default int getMaxSectionY() {
    return SectionPos.blockToSectionCoord(this.getMaxY());
  }

  public default boolean isInsideBuildHeight(BlockPos pos) {
    return this.isInsideBuildHeight(pos.getY());
  }

  public default boolean isInsideBuildHeight(int blockY) {
    return blockY >= this.getMinY() && blockY <= this.getMaxY();
  }

  public default boolean isOutsideBuildHeight(BlockPos pos) {
    return this.isOutsideBuildHeight(pos.getY());
  }

  public default boolean isOutsideBuildHeight(int blockY) {
    return blockY < this.getMinY() || blockY > this.getMaxY();
  }

  public default int getSectionIndex(int blockY) {
    return this.getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(blockY));
  }

  public default int getSectionIndexFromSectionY(int sectionY) {
    return sectionY - this.getMinSectionY();
  }

  public default int getSectionYFromSectionIndex(int sectionIndex) {
    return sectionIndex + this.getMinSectionY();
  }

  public static LevelHeightAccessor create(final int minY, final int height) {
    return new LevelHeightAccessor() {

      @Override
      public int getHeight() {
        return height;
      }

      @Override
      public int getMinY() {
        return minY;
      }
    };
  }
}
