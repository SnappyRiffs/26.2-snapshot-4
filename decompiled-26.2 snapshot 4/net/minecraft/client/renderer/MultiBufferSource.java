/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2IntMap
 *  it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.SequencedSet;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public interface MultiBufferSource {
    public static BufferSource create(int initialBufferSize, SequencedSet<RenderType> fixedTypes) {
        return new BufferSource(initialBufferSize, fixedTypes);
    }

    public VertexConsumer getBuffer(RenderType var1);

    public static class BufferSource
    implements MultiBufferSource,
    AutoCloseable {
        private static final int NO_FIXED_DRAW = -1;
        private final StagedVertexBuffer stagedBuffer;
        private final SequencedSet<RenderType> fixedTypes;
        private final List<StagedVertexBuffer.Draw> draws = new ArrayList<StagedVertexBuffer.Draw>();
        private final List<RenderType> drawTypes = new ArrayList<RenderType>();
        private final Reference2IntMap<RenderType> fixedDraws = new Reference2IntOpenHashMap();

        protected BufferSource(int initialBufferSize, SequencedSet<RenderType> fixedTypes) {
            this.stagedBuffer = new StagedVertexBuffer(() -> "MultiBufferSource", initialBufferSize);
            this.fixedTypes = fixedTypes;
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            if (!this.drawTypes.isEmpty() && this.drawTypes.getLast() == renderType && renderType.canConsolidateConsecutiveGeometry()) {
                return this.stagedBuffer.getVertexBuilder(this.draws.getLast());
            }
            int fixedDrawIndex = this.fixedDraws.getOrDefault((Object)renderType, -1);
            StagedVertexBuffer.Draw draw = fixedDrawIndex != -1 ? this.draws.get(fixedDrawIndex) : this.appendDraw(renderType);
            return this.stagedBuffer.getVertexBuilder(draw);
        }

        private StagedVertexBuffer.Draw appendDraw(RenderType renderType) {
            StagedVertexBuffer.Draw draw = this.stagedBuffer.appendDraw(renderType.format(), renderType.mode(), renderType.sortOnUpload() ? RenderSystem.getProjectionType().vertexSorting() : null);
            this.draws.add(draw);
            this.drawTypes.add(renderType);
            if (this.fixedTypes.contains(renderType)) {
                this.fixedDraws.put((Object)renderType, this.draws.size() - 1);
            }
            return draw;
        }

        public void uploadAndDraw() {
            if (this.draws.isEmpty()) {
                return;
            }
            ProfilerFiller profiler = Profiler.get();
            profiler.push("uploadMultiBufferSource");
            this.stagedBuffer.upload();
            profiler.popPush("drawMultiBufferSource");
            for (int i = 0; i < this.draws.size(); ++i) {
                StagedVertexBuffer.ExecuteInfo info;
                StagedVertexBuffer.Draw draw = this.draws.get(i);
                RenderType type = this.drawTypes.get(i);
                if (this.fixedDraws.containsKey((Object)type) || (info = this.stagedBuffer.getExecuteInfo(draw)) == null) continue;
                type.drawFromBuffer(info);
            }
            for (RenderType fixedType : this.fixedTypes) {
                StagedVertexBuffer.Draw draw;
                StagedVertexBuffer.ExecuteInfo info;
                int index = this.fixedDraws.getOrDefault((Object)fixedType, -1);
                if (index == -1 || (info = this.stagedBuffer.getExecuteInfo(draw = this.draws.get(index))) == null) continue;
                fixedType.drawFromBuffer(info);
            }
            this.stagedBuffer.endDraw();
            profiler.pop();
            this.draws.clear();
            this.drawTypes.clear();
            this.fixedDraws.clear();
        }

        public void endFrame() {
            this.stagedBuffer.endFrame();
        }

        @Override
        public void close() {
            this.stagedBuffer.close();
        }
    }
}

