/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class BlockModelFeatureRenderer {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final QuadInstance quadInstance = new QuadInstance();

    public void renderSolid(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
        this.renderModels(nodeCollection, context, false);
    }

    public void renderTranslucent(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
        this.renderModels(nodeCollection, context, true);
    }

    private void renderModels(SubmitNodeCollection nodeCollection, FeatureFrameContext context, boolean translucent) {
        for (Submit submit : nodeCollection.getBlockModelSubmits()) {
            VertexConsumer outlineBuffer;
            VertexConsumer wrappedBuffer;
            if (submit.renderType().hasBlending() != translucent) continue;
            VertexConsumer buffer = context.bufferSource().getBuffer(submit.renderType());
            VertexConsumer vertexConsumer = wrappedBuffer = submit.sheetedDecalPose() != null ? new SheetedDecalTextureGenerator(buffer, submit.sheetedDecalPose(), 1.0f) : buffer;
            if (submit.outlineColor() != 0) {
                context.outlineBufferSource().setColor(submit.outlineColor());
                outlineBuffer = context.outlineBufferSource().getBuffer(submit.renderType());
            } else {
                outlineBuffer = null;
            }
            this.quadInstance.setLightCoords(submit.lightCoords());
            this.quadInstance.setOverlayCoords(submit.overlayCoords());
            for (BlockStateModelPart part : submit.modelParts()) {
                BlockModelFeatureRenderer.putPartQuads(part, submit.pose(), this.quadInstance, submit.tintLayers(), wrappedBuffer, outlineBuffer);
            }
        }
    }

    private static void putPartQuads(BlockStateModelPart part, PoseStack.Pose pose, QuadInstance quadInstance, int[] tintLayers, VertexConsumer buffer, @Nullable VertexConsumer outlineBuffer) {
        for (Direction direction : DIRECTIONS) {
            for (BakedQuad quad : part.getQuads(direction)) {
                BlockModelFeatureRenderer.putQuad(pose, quad, quadInstance, tintLayers, buffer, outlineBuffer);
            }
        }
        for (BakedQuad quad : part.getQuads(null)) {
            BlockModelFeatureRenderer.putQuad(pose, quad, quadInstance, tintLayers, buffer, outlineBuffer);
        }
    }

    private static void putQuad(PoseStack.Pose pose, BakedQuad quad, QuadInstance instance, int[] tintLayers, VertexConsumer buffer, @Nullable VertexConsumer outlineBuffer) {
        int tintIndex = quad.materialInfo().tintIndex();
        boolean tintColor = tintIndex != -1 && tintIndex < tintLayers.length;
        instance.setColor(tintColor ? tintLayers[tintIndex] : -1);
        buffer.putBakedQuad(pose, quad, instance);
        if (outlineBuffer != null) {
            outlineBuffer.putBakedQuad(pose, quad, instance);
        }
    }

    public record Submit(PoseStack.Pose pose, RenderType renderType, List<BlockStateModelPart> modelParts, int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor, @Nullable PoseStack.Pose sheetedDecalPose) {
    }
}

