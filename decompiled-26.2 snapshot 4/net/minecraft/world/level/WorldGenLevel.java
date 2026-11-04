/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.world.level;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public interface WorldGenLevel extends ServerLevelAccessor {
  public long getSeed();

  public default boolean ensureCanWrite(BlockPos pos) {
    return true;
  }

  public default void setCurrentlyGenerating(@Nullable Supplier<String> currentlyGenerating) {}
}
