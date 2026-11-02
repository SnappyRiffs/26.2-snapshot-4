/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSortedSets
 */
package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.util.SequencedSet;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.util.Util;

public class RenderBuffers implements AutoCloseable {
  private final SectionBufferBuilderPack fixedBufferPack = new SectionBufferBuilderPack();
  private final SectionBufferBuilderPool sectionBufferPool;
  private final MultiBufferSource.BufferSource bufferSource;
  private final OutlineBufferSource outlineBufferSource;

  public RenderBuffers(int maxSectionBuilders) {
    this.sectionBufferPool = SectionBufferBuilderPool.allocate(maxSectionBuilders);
    SequencedSet fixedTypes =
        (SequencedSet)
            Util.make(
                new ObjectLinkedOpenHashSet(),
                types -> {
                  types.add((Object) Sheets.cutoutBlockItemSheet());
                  types.add((Object) Sheets.translucentBlockItemSheet());
                  types.add((Object) Sheets.cutoutItemSheet());
                  types.add((Object) Sheets.translucentItemSheet());
                  types.add((Object) RenderTypes.glint());
                  types.add((Object) RenderTypes.glintTranslucent());
                  types.add((Object) RenderTypes.waterMask());
                  types.addAll(ModelBakery.DESTROY_TYPES);
                });
    this.bufferSource = MultiBufferSource.create(786432, fixedTypes);
    this.outlineBufferSource =
        new OutlineBufferSource(
            MultiBufferSource.create(1536, (SequencedSet<RenderType>) ObjectSortedSets.emptySet()));
  }

  public SectionBufferBuilderPack fixedBufferPack() {
    return this.fixedBufferPack;
  }

  public SectionBufferBuilderPool sectionBufferPool() {
    return this.sectionBufferPool;
  }

  public MultiBufferSource.BufferSource bufferSource() {
    return this.bufferSource;
  }

  public OutlineBufferSource outlineBufferSource() {
    return this.outlineBufferSource;
  }

  public void endFrame() {
    this.bufferSource.endFrame();
    this.outlineBufferSource.endFrame();
  }

  @Override
  public void close() {
    this.bufferSource.close();
    this.outlineBufferSource.close();
  }
}
