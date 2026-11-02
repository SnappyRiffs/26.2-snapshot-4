/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  org.joml.Matrix4fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public class ModelFeatureRenderer {
    private final PoseStack poseStack = new PoseStack();

    public void renderSolid(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
        Storage storage = nodeCollection.getModelSubmits();
        this.renderBatch(context, storage.solidModelSubmits);
    }

    public void renderTranslucent(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
        Storage storage = nodeCollection.getModelSubmits();
        storage.translucentModelSubmits.sort(Comparator.comparingDouble(submit -> -submit.distanceToCameraSq()));
        this.renderTranslucents(context, storage.translucentModelSubmits);
    }

    private void renderTranslucents(FeatureFrameContext context, List<Submit<?>> submits) {
        for (Submit<?> submit : submits) {
            this.renderModel(submit, submit.renderType(), context.bufferSource(), context.outlineBufferSource());
        }
    }

    private void renderBatch(FeatureFrameContext context, Map<RenderType, List<Submit<?>>> map) {
        Collection<Map.Entry<RenderType, List<Submit<?>>>> entries;
        if (SharedConstants.DEBUG_SHUFFLE_MODELS) {
            ArrayList shuffledCopy = new ArrayList(map.entrySet());
            Collections.shuffle(shuffledCopy);
            entries = shuffledCopy;
        } else {
            entries = map.entrySet();
        }
        for (Map.Entry entry : entries) {
            for (Submit submit : (List)entry.getValue()) {
                this.renderModel(submit, (RenderType)entry.getKey(), context.bufferSource(), context.outlineBufferSource());
            }
        }
    }

    private <S> void renderModel(Submit<S> submit, RenderType renderType, MultiBufferSource bufferSource, OutlineBufferSource outlineBufferSource) {
        this.poseStack.pushPose();
        this.poseStack.last().set(submit.pose());
        VertexConsumer buffer = bufferSource.getBuffer(renderType);
        Model<S> model = submit.model();
        VertexConsumer wrappedBuffer = submit.sprite() == null ? buffer : submit.sprite().wrap(buffer);
        model.setupAnim(submit.state());
        model.renderToBuffer(this.poseStack, wrappedBuffer, submit.lightCoords(), submit.overlayCoords(), submit.tintedColor());
        if (submit.outlineColor() != 0 && (renderType.outline().isPresent() || renderType.isOutline())) {
            outlineBufferSource.setColor(submit.outlineColor());
            VertexConsumer outlineBuffer = outlineBufferSource.getBuffer(renderType);
            model.renderToBuffer(this.poseStack, submit.sprite() == null ? outlineBuffer : submit.sprite().wrap(outlineBuffer), submit.lightCoords(), submit.overlayCoords(), submit.tintedColor());
        }
        if (submit.crumblingOverlay() != null && renderType.affectsCrumbling()) {
            SheetedDecalTextureGenerator breakingBuffer = new SheetedDecalTextureGenerator(bufferSource.getBuffer(ModelBakery.DESTROY_TYPES.get(submit.crumblingOverlay().progress())), submit.crumblingOverlay().cameraPose(), 1.0f);
            model.renderToBuffer(this.poseStack, submit.sprite() == null ? breakingBuffer : submit.sprite().wrap(breakingBuffer), submit.lightCoords(), submit.overlayCoords(), submit.tintedColor());
        }
        this.poseStack.popPose();
    }

    public static class Storage {
        private final Map<RenderType, List<Submit<?>>> solidModelSubmits = new HashMap();
        private final List<Submit<?>> translucentModelSubmits = new ArrayList();
        private final Set<RenderType> usedModelSubmitBuckets = new ObjectOpenHashSet();

        public void add(RenderType renderType, Submit<?> submit) {
            if (!renderType.hasBlending()) {
                this.solidModelSubmits.computeIfAbsent(renderType, ignored -> new ArrayList()).add(submit);
            } else {
                this.translucentModelSubmits.add(submit);
            }
        }

        public void clear() {
            this.translucentModelSubmits.clear();
            for (Map.Entry<RenderType, List<Submit<?>>> bucketEntry : this.solidModelSubmits.entrySet()) {
                List<Submit<?>> bucket = bucketEntry.getValue();
                if (bucket.isEmpty()) continue;
                this.usedModelSubmitBuckets.add(bucketEntry.getKey());
                bucket.clear();
            }
        }

        public void endFrame() {
            this.solidModelSubmits.keySet().removeIf(renderType -> !this.usedModelSubmitBuckets.contains(renderType));
            this.usedModelSubmitBuckets.clear();
        }
    }

    public record Submit<S>(RenderType renderType, PoseStack.Pose pose, Model<? super S> model, S state, int lightCoords, int overlayCoords, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor, @Nullable CrumblingOverlay crumblingOverlay) implements TranslucentSubmit
    {
        @Override
        public float distanceToCameraSq() {
            return TranslucentSubmit.computeDistanceToCameraSq((Matrix4fc)this.pose.pose());
        }
    }

    public record CrumblingOverlay(int progress, PoseStack.Pose cameraPose) {
    }
}

