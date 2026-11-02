/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public interface SectionMesh extends AutoCloseable {
  public default boolean isDifferentPointOfView(TranslucencyPointOfView pointOfView) {
    return false;
  }

  public default boolean hasRenderableLayers() {
    return false;
  }

  public default boolean hasTranslucentGeometry() {
    return false;
  }

  public default boolean isEmpty(ChunkSectionLayer layer) {
    return true;
  }

  public default List<BlockEntity> getRenderableBlockEntities() {
    return Collections.emptyList();
  }

  public boolean facesCanSeeEachother(Direction var1, Direction var2);

  public default @Nullable SectionDraw getSectionDraw(ChunkSectionLayer layer) {
    return null;
  }

  @Override
  public default void close() {}

  public record SectionDraw(
      int indexCount, VertexFormat.IndexType indexType, boolean hasCustomIndexBuffer) {}
}
